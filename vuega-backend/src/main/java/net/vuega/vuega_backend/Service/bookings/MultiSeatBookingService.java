package net.vuega.vuega_backend.Service.bookings;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import net.vuega.vuega_backend.Model.bookings.Booking;
import net.vuega.vuega_backend.Model.bookings.BookingPassenger;
import net.vuega.vuega_backend.Model.bookings.BookingStatus;
import net.vuega.vuega_backend.Model.passengers.Passenger;
import net.vuega.vuega_backend.Model.seats.lock.SeatLock;
import net.vuega.vuega_backend.Model.seats.session.BookingSession;
import net.vuega.vuega_backend.Repository.bookings.BookingPassengerRepository;
import net.vuega.vuega_backend.Repository.bookings.BookingRepository;
import net.vuega.vuega_backend.Repository.passengers.PassengerRepository;
import net.vuega.vuega_backend.Repository.seats.bookings.SeatBookingRepository;
import net.vuega.vuega_backend.Repository.seats.lock.SeatLockRepository;
import net.vuega.vuega_backend.Repository.seats.session.BookingSessionRepository;
import net.vuega.vuega_backend.Service.seats.socket.SeatSocketService;
import net.vuega.vuega_backend.exception.InvalidStopRangeException;
import net.vuega.vuega_backend.exception.SeatMismatchException;
import net.vuega.vuega_backend.exception.SeatNotAvailableException;
import net.vuega.vuega_backend.exception.SessionExpiredException;
import net.vuega.vuega_backend.exception.SessionNotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class MultiSeatBookingService {

        private static final long GRACE_PERIOD_SECONDS = 30;

        private final BookingSessionRepository sessionRepository;
        private final SeatLockRepository lockRepository;
        private final BookingRepository bookingRepository;
        private final SeatBookingRepository seatBookingRepository;
        private final PassengerRepository passengerRepository;
        private final BookingPassengerRepository bookingPassengerRepository;
        private final SeatSocketService socketService;

        // Validates session, locks, and segments, then atomically creates the booking.
        // Rolls back entirely on any failure — no partial commits.
        @Transactional
        public MultiSeatBookingResponse createBooking(MultiSeatBookingRequest request) {

                for (PassengerRequest pr : request.getPassengerDetails()) {
                        if (pr.getFromStopOrder() >= pr.getToStopOrder()) {
                                throw new InvalidStopRangeException(
                                                "fromStopOrder must be less than toStopOrder for passenger '"
                                                                + pr.getName() + "' on seat " + pr.getSeatId());
                        }
                }

                if (request.getIdempotencyKey() != null && !request.getIdempotencyKey().isBlank()) {
                        Optional<Booking> existing = bookingRepository
                                        .findByIdempotencyKey(request.getIdempotencyKey());
                        if (existing.isPresent()) {
                                log.info("createBooking: idempotency hit key={}", request.getIdempotencyKey());
                                return buildResponseFromExisting(existing.get());
                        }
                }

                BookingSession session = sessionRepository.findById(request.getSessionId())
                                .orElseThrow(() -> new SessionNotFoundException(request.getSessionId()));

                LocalDateTime graceDeadline = session.getExpiresAt().plusSeconds(GRACE_PERIOD_SECONDS);
                if (graceDeadline.isBefore(LocalDateTime.now())) {
                        throw new SessionExpiredException(
                                        "Session " + request.getSessionId() + " has expired.");
                }

                List<SeatLock> sessionLocks = lockRepository.findBySessionIdWithSeat(request.getSessionId());

                Set<Long> lockedSeatIds = sessionLocks.stream()
                                .map(lock -> lock.getSeat().getSeatId())
                                .collect(Collectors.toSet());

                Set<Long> requestedSeatIds = request.getPassengerDetails().stream()
                                .map(PassengerRequest::getSeatId)
                                .collect(Collectors.toSet());

                if (!lockedSeatIds.equals(requestedSeatIds)) {
                        throw new SeatMismatchException(
                                        "Passenger seatIds " + requestedSeatIds
                                                        + " do not match locked seatIds " + lockedSeatIds
                                                        + " for session " + request.getSessionId() + ".");
                }

                var lockBySeatId = sessionLocks.stream()
                                .collect(Collectors.toMap(lock -> lock.getSeat().getSeatId(), lock -> lock));

                var bookedStatus = net.vuega.vuega_backend.Model.seats.bookings.BookingStatus.BOOKED;
                for (PassengerRequest pr : request.getPassengerDetails()) {
                        long overlapping = seatBookingRepository.countOverlappingBookings(
                                        pr.getSeatId(),
                                        session.getScheduleId(),
                                        pr.getFromStopOrder(),
                                        pr.getToStopOrder(),
                                        bookedStatus);

                        if (overlapping > 0) {
                                SeatLock lock = lockBySeatId.get(pr.getSeatId());
                                throw new SeatNotAvailableException(
                                                "Seat " + pr.getSeatId()
                                                                + " (seat no: " + lock.getSeat().getSeatNo()
                                                                + ") is already booked for segment "
                                                                + pr.getFromStopOrder() + "→" + pr.getToStopOrder()
                                                                + ".");
                        }
                }

                String pnr = generatePnr();
                BigDecimal totalAmount = sessionLocks.stream()
                                .map(lock -> lock.getSeat().getBasePrice())
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                Booking mainBooking = Booking.builder()
                                .pnr(pnr)
                                .partnerId(request.getPartnerId())
                                .scheduleId(session.getScheduleId())
                                .status(BookingStatus.CONFIRMED)
                                .totalAmount(totalAmount)
                                .idempotencyKey(request.getIdempotencyKey())
                                .build();
                mainBooking = bookingRepository.save(mainBooking);

                List<Passenger> passengers = new ArrayList<>();
                List<net.vuega.vuega_backend.Model.seats.bookings.Booking> seatBookings = new ArrayList<>();

                for (PassengerRequest pr : request.getPassengerDetails()) {
                        Passenger passenger = Passenger.builder()
                                        .name(pr.getName())
                                        .age(pr.getAge())
                                        .gender(pr.getGender())
                                        .build();
                        passenger = passengerRepository.save(passenger);
                        passengers.add(passenger);

                        BookingPassenger bp = BookingPassenger.builder()
                                        .bookingId(mainBooking.getBookingId())
                                        .passengerId(passenger.getPassengerId())
                                        .build();
                        bookingPassengerRepository.save(bp);

                        SeatLock lock = lockBySeatId.get(pr.getSeatId());
                        net.vuega.vuega_backend.Model.seats.bookings.Booking seatBooking = net.vuega.vuega_backend.Model.seats.bookings.Booking
                                        .builder()
                                        .bookingId(mainBooking.getBookingId())
                                        .seat(lock.getSeat())
                                        .scheduleId(session.getScheduleId())
                                        .passengerId(passenger.getPassengerId())
                                        .fromStopOrder(pr.getFromStopOrder())
                                        .toStopOrder(pr.getToStopOrder())
                                        .status(bookedStatus)
                                        .build();
                        seatBookings.add(seatBookingRepository.save(seatBooking));
                }

                sessionRepository.delete(session);

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
                                pnr, seatBookings.size(), passengers.size());

                return buildResponse(mainBooking, passengers, seatBookings);
        }

        // Builds the API response DTO from freshly-created entities.
        private MultiSeatBookingResponse buildResponse(
                        Booking mainBooking,
                        List<Passenger> passengers,
                        List<net.vuega.vuega_backend.Model.seats.bookings.Booking> seatBookings) {

                List<PassengerDTO> passengerDTOs = passengers.stream()
                                .map(p -> PassengerDTO.builder()
                                                .passengerId(p.getPassengerId())
                                                .name(p.getName())
                                                .age(p.getAge())
                                                .gender(p.getGender())
                                                .build())
                                .toList();

                List<BookingDTO> seatBookingDTOs = seatBookings.stream()
                                .map(sb -> BookingDTO.builder()
                                                .seatStatusId(sb.getSeatStatusId())
                                                .bookingId(sb.getBookingId())
                                                .seatId(sb.getSeat().getSeatId())
                                                .seatNo(sb.getSeat().getSeatNo())
                                                .busId(sb.getSeat().getBusId())
                                                .scheduleId(sb.getScheduleId())
                                                .passengerId(sb.getPassengerId())
                                                .fromStopOrder(sb.getFromStopOrder())
                                                .toStopOrder(sb.getToStopOrder())
                                                .status(sb.getStatus())
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

        // Reconstructs a response from a previously persisted booking (idempotency
        // hit).
        private MultiSeatBookingResponse buildResponseFromExisting(Booking mainBooking) {
                List<Long> passengerIds = bookingPassengerRepository
                                .findByBookingId(mainBooking.getBookingId())
                                .stream()
                                .map(BookingPassenger::getPassengerId)
                                .toList();
                List<Passenger> passengers = passengerRepository.findAllById(passengerIds);

                List<PassengerDTO> passengerDTOs = passengers.stream()
                                .map(p -> PassengerDTO.builder()
                                                .passengerId(p.getPassengerId())
                                                .name(p.getName())
                                                .age(p.getAge())
                                                .gender(p.getGender())
                                                .build())
                                .toList();

                // Look up seat bookings for this booking
                List<BookingDTO> seatBookingDTOs = seatBookingRepository
                                .findByBookingId(mainBooking.getBookingId())
                                .stream()
                                .map(sb -> BookingDTO.builder()
                                                .seatStatusId(sb.getSeatStatusId())
                                                .bookingId(sb.getBookingId())
                                                .seatId(sb.getSeat().getSeatId())
                                                .seatNo(sb.getSeat().getSeatNo())
                                                .busId(sb.getSeat().getBusId())
                                                .scheduleId(sb.getScheduleId())
                                                .passengerId(sb.getPassengerId())
                                                .fromStopOrder(sb.getFromStopOrder())
                                                .toStopOrder(sb.getToStopOrder())
                                                .status(sb.getStatus())
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

        private String generatePnr() {
                return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
}
