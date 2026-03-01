package net.vuega.vuega_backend.Service.bookings;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.vuega.vuega_backend.DTO.bookings.MultiSeatBookingRequest;
import net.vuega.vuega_backend.DTO.bookings.MultiSeatBookingResponse;
import net.vuega.vuega_backend.DTO.bookings.PassengerRequest;
import net.vuega.vuega_backend.DTO.passengers.PassengerDTO;
import net.vuega.vuega_backend.DTO.seats.seat.bookings.BookingDTO;
import net.vuega.vuega_backend.DTO.seats.socket.SeatUpdateMessage;
import net.vuega.vuega_backend.Exception.InvalidStopRangeException;
import net.vuega.vuega_backend.Exception.SeatMismatchException;
import net.vuega.vuega_backend.Exception.SeatNotAvailableException;
import net.vuega.vuega_backend.Exception.SessionExpiredException;
import net.vuega.vuega_backend.Exception.SessionNotFoundException;
import net.vuega.vuega_backend.Model.bookings.Booking;
import net.vuega.vuega_backend.Model.bookings.BookingStatus;
import net.vuega.vuega_backend.Model.passengers.Passenger;
import net.vuega.vuega_backend.Model.seats.lock.SeatLock;
import net.vuega.vuega_backend.Model.seats.session.BookingSession;
import net.vuega.vuega_backend.Repository.bookings.BookingRepository;
import net.vuega.vuega_backend.Repository.passengers.PassengerRepository;
import net.vuega.vuega_backend.Repository.seats.lock.SeatLockRepository;
import net.vuega.vuega_backend.Repository.seats.session.BookingSessionRepository;
import net.vuega.vuega_backend.Service.seats.socket.SeatSocketService;

@Service
@RequiredArgsConstructor
@Slf4j
public class MultiSeatBookingService {

    private static final long GRACE_PERIOD_SECONDS = 30;

    private final BookingSessionRepository sessionRepository;
    private final SeatLockRepository lockRepository;
    private final BookingRepository bookingRepository;
    private final net.vuega.vuega_backend.Repository.seats.bookings.BookingRepository seatBookingRepository;
    private final PassengerRepository passengerRepository;
    private final SeatSocketService socketService;

    /**
     * Atomic multi-seat booking — ONE @Transactional method.
     *
     * If ANY step fails, the entire transaction rolls back automatically.
     * No manual revert logic. No partial commits.
     */
    @Transactional
    public MultiSeatBookingResponse createBooking(MultiSeatBookingRequest request) {
        // --- Input validation ---
        if (request.getFromStopOrder() >= request.getToStopOrder()) {
            throw new InvalidStopRangeException("fromStopOrder must be less than toStopOrder");
        }

        // --- Idempotency check ---
        if (request.getIdempotencyKey() != null && !request.getIdempotencyKey().isBlank()) {
            Optional<Booking> existing = bookingRepository.findByIdempotencyKey(request.getIdempotencyKey());
            if (existing.isPresent()) {
                log.info("createBooking: idempotency hit key={}", request.getIdempotencyKey());
                return buildResponseFromExisting(existing.get());
            }
        }

        // -----------------------------------------------------------
        // Step 1 — Validate Session
        // -----------------------------------------------------------
        BookingSession session = sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new SessionNotFoundException(request.getSessionId()));

        LocalDateTime graceDeadline = session.getExpiresAt().plusSeconds(GRACE_PERIOD_SECONDS);
        if (graceDeadline.isBefore(LocalDateTime.now())) {
            throw new SessionExpiredException(
                    "Session " + request.getSessionId() + " has expired.");
        }

        // -----------------------------------------------------------
        // Step 2 — Validate SeatLocks match request exactly
        // -----------------------------------------------------------
        List<SeatLock> sessionLocks = lockRepository.findBySessionIdWithSeat(request.getSessionId());

        Set<Long> lockedSeatIds = sessionLocks.stream()
                .map(lock -> lock.getSeat().getSeatId())
                .collect(Collectors.toSet());

        Set<Long> requestedSeatIds = new HashSet<>(request.getSeatIds());

        if (!lockedSeatIds.equals(requestedSeatIds)) {
            throw new SeatMismatchException(
                    "Requested seatIds " + requestedSeatIds
                            + " do not match locked seatIds " + lockedSeatIds
                            + " for session " + request.getSessionId() + ".");
        }

        // -----------------------------------------------------------
        // Step 3 — Validate no overlapping bookings for each seat
        // -----------------------------------------------------------
        for (SeatLock lock : sessionLocks) {
            long overlapping = seatBookingRepository.countOverlappingBookings(
                    lock.getSeat().getSeatId(),
                    session.getScheduleId(),
                    request.getFromStopOrder(),
                    request.getToStopOrder(),
                    net.vuega.vuega_backend.Model.seats.bookings.BookingStatus.BOOKED);

            if (overlapping > 0) {
                throw new SeatNotAvailableException(
                        "Seat " + lock.getSeat().getSeatId()
                                + " (seat no: " + lock.getSeat().getSeatNo()
                                + ") is already booked for this segment.");
            }
        }

        // -----------------------------------------------------------
        // Step 4 — Insert all booking data in same transaction
        // -----------------------------------------------------------

        // 4.1 — Generate PNR and insert Booking record
        String pnr = generatePnr();
        BigDecimal totalAmount = sessionLocks.stream()
                .map(lock -> lock.getSeat().getBasePrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Booking mainBooking = Booking.builder()
                .pnr(pnr)
                .partnerId(session.getPassengerId())
                .scheduleId(session.getScheduleId())
                .status(BookingStatus.CONFIRMED)
                .totalAmount(totalAmount)
                .idempotencyKey(request.getIdempotencyKey())
                .build();
        mainBooking = bookingRepository.save(mainBooking);

        // 4.2 — Insert Passenger records
        List<Passenger> passengers = new ArrayList<>();
        for (PassengerRequest pr : request.getPassengerDetails()) {
            Passenger passenger = Passenger.builder()
                    .booking(mainBooking)
                    .name(pr.getName())
                    .age(pr.getAge())
                    .gender(pr.getGender())
                    .build();
            passengers.add(passengerRepository.save(passenger));
        }

        // 4.3 — Insert SeatBooking records (one per seat)
        List<net.vuega.vuega_backend.Model.seats.bookings.Booking> seatBookings = new ArrayList<>();
        int passengerIndex = 0;
        for (SeatLock lock : sessionLocks) {
            // Associate passenger to seat in order; cycle if more seats than passengers
            Long passengerId = passengers.get(passengerIndex % passengers.size()).getPassengerId();

            net.vuega.vuega_backend.Model.seats.bookings.Booking seatBooking = net.vuega.vuega_backend.Model.seats.bookings.Booking
                    .builder()
                    .seat(lock.getSeat())
                    .scheduleId(session.getScheduleId())
                    .passengerId(passengerId)
                    .fromStopOrder(request.getFromStopOrder())
                    .toStopOrder(request.getToStopOrder())
                    .status(net.vuega.vuega_backend.Model.seats.bookings.BookingStatus.BOOKED)
                    .idempotencyKey(null) // idempotency is on main booking
                    .build();
            seatBookings.add(seatBookingRepository.save(seatBooking));
            passengerIndex++;
        }

        // -----------------------------------------------------------
        // Step 5 — Delete Session (locks cascade-delete automatically)
        // -----------------------------------------------------------
        sessionRepository.delete(session);

        // Broadcast BOOKED events for each seat
        for (net.vuega.vuega_backend.Model.seats.bookings.Booking sb : seatBookings) {
            socketService.broadcast(SeatUpdateMessage.builder()
                    .event(SeatUpdateMessage.Event.BOOKED)
                    .busId(sb.getSeat().getBusId())
                    .seatId(sb.getSeat().getSeatId())
                    .seatNo(sb.getSeat().getSeatNo())
                    .scheduleId(sb.getScheduleId())
                    .fromStopOrder(sb.getFromStopOrder())
                    .toStopOrder(sb.getToStopOrder())
                    .timestamp(LocalDateTime.now())
                    .build());
        }

        log.info("Multi-seat booking created: PNR={}, seats={}, passengers={}",
                pnr, request.getSeatIds().size(), passengers.size());

        return buildResponse(mainBooking, passengers, seatBookings);
    }

    // ---------------------------------------------------------------
    // Response builders
    // ---------------------------------------------------------------

    private MultiSeatBookingResponse buildResponse(
            Booking mainBooking,
            List<Passenger> passengers,
            List<net.vuega.vuega_backend.Model.seats.bookings.Booking> seatBookings) {

        List<PassengerDTO> passengerDTOs = passengers.stream()
                .map(p -> PassengerDTO.builder()
                        .passengerId(p.getPassengerId())
                        .bookingId(p.getBooking().getBookingId())
                        .name(p.getName())
                        .age(p.getAge())
                        .gender(p.getGender())
                        .build())
                .toList();

        List<BookingDTO> seatBookingDTOs = seatBookings.stream()
                .map(sb -> BookingDTO.builder()
                        .seatStatusId(sb.getSeatStatusId())
                        .seatId(sb.getSeat().getSeatId())
                        .seatNo(sb.getSeat().getSeatNo())
                        .busId(sb.getSeat().getBusId())
                        .scheduleId(sb.getScheduleId())
                        .passengerId(sb.getPassengerId())
                        .fromStopOrder(sb.getFromStopOrder())
                        .toStopOrder(sb.getToStopOrder())
                        .status(sb.getStatus())
                        .idempotencyKey(sb.getIdempotencyKey())
                        .createdAt(sb.getCreatedAt())
                        .updatedAt(sb.getUpdatedAt())
                        .build())
                .toList();

        return MultiSeatBookingResponse.builder()
                .bookingId(mainBooking.getBookingId())
                .pnr(mainBooking.getPnr())
                .partnerId(mainBooking.getPartnerId())
                .scheduleId(mainBooking.getScheduleId())
                .status(mainBooking.getStatus())
                .totalAmount(mainBooking.getTotalAmount())
                .idempotencyKey(mainBooking.getIdempotencyKey())
                .createdAt(mainBooking.getCreatedAt())
                .passengers(passengerDTOs)
                .seatBookings(seatBookingDTOs)
                .build();
    }

    private MultiSeatBookingResponse buildResponseFromExisting(Booking mainBooking) {
        List<Passenger> passengers = passengerRepository.findByBookingBookingId(mainBooking.getBookingId());

        List<PassengerDTO> passengerDTOs = passengers.stream()
                .map(p -> PassengerDTO.builder()
                        .passengerId(p.getPassengerId())
                        .bookingId(p.getBooking().getBookingId())
                        .name(p.getName())
                        .age(p.getAge())
                        .gender(p.getGender())
                        .build())
                .toList();

        return MultiSeatBookingResponse.builder()
                .bookingId(mainBooking.getBookingId())
                .pnr(mainBooking.getPnr())
                .partnerId(mainBooking.getPartnerId())
                .scheduleId(mainBooking.getScheduleId())
                .status(mainBooking.getStatus())
                .totalAmount(mainBooking.getTotalAmount())
                .idempotencyKey(mainBooking.getIdempotencyKey())
                .createdAt(mainBooking.getCreatedAt())
                .passengers(passengerDTOs)
                .build();
    }

    private String generatePnr() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
