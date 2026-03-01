package net.vuega.vuega_backend.Service.seats.lock;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.vuega.vuega_backend.DTO.seats.lock.AcquireLockRequest;
import net.vuega.vuega_backend.DTO.seats.lock.SeatLockDTO;
import net.vuega.vuega_backend.DTO.seats.seat.bookings.BookingDTO;
import net.vuega.vuega_backend.DTO.seats.session.BookingSessionDTO;
import net.vuega.vuega_backend.DTO.seats.socket.SeatUpdateMessage;
import net.vuega.vuega_backend.Exception.BookingNotFoundException;
import net.vuega.vuega_backend.Exception.SeatLockConflictException;
import net.vuega.vuega_backend.Exception.SeatLockNotFoundException;
import net.vuega.vuega_backend.Exception.SeatNotFoundException;
import net.vuega.vuega_backend.Exception.SessionExpiredException;
import net.vuega.vuega_backend.Exception.SessionNotFoundException;
import net.vuega.vuega_backend.Model.seats.bookings.Booking;
import net.vuega.vuega_backend.Model.seats.bookings.BookingStatus;
import net.vuega.vuega_backend.Model.seats.lock.SeatLock;
import net.vuega.vuega_backend.Model.seats.seat.Seat;
import net.vuega.vuega_backend.Model.seats.session.BookingSession;
import net.vuega.vuega_backend.Repository.seats.bookings.BookingRepository;
import net.vuega.vuega_backend.Repository.seats.lock.SeatLockRepository;
import net.vuega.vuega_backend.Repository.seats.seat.SeatRepository;
import net.vuega.vuega_backend.Repository.seats.session.BookingSessionRepository;
import net.vuega.vuega_backend.Service.seats.socket.SeatSocketService;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatLockService {

        private static final int SESSION_TTL_MINUTES = 10;

        private final SeatRepository seatRepository;
        private final SeatLockRepository lockRepository;
        private final BookingRepository bookingRepository;
        private final BookingSessionRepository sessionRepository;
        private final SeatSocketService socketService;

        /**
         * Acquire a lock on a single seat.
         * If sessionId is not provided, a new BookingSession is created.
         * If sessionId is provided, the session is validated and its expiry extended.
         * Concurrency is handled by the unique constraint on (seat_id, schedule_id).
         */
        @Transactional
        public SeatLockDTO acquireLock(Long seatId, AcquireLockRequest request) {
                Seat seat = seatRepository.findById(seatId)
                                .orElseThrow(() -> new SeatNotFoundException(seatId));

                BookingSession session;
                if (request.getSessionId() != null) {
                        // Validate existing session
                        session = sessionRepository.findById(request.getSessionId())
                                        .orElseThrow(() -> new SessionNotFoundException(request.getSessionId()));
                        if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
                                throw new SessionExpiredException(
                                                "Session " + request.getSessionId() + " has expired.");
                        }
                        // Extend session expiry on every lock
                        session.setExpiresAt(LocalDateTime.now().plusMinutes(SESSION_TTL_MINUTES));
                        sessionRepository.save(session);
                } else {
                        // Create new session
                        session = BookingSession.builder()
                                        .passengerId(request.getPassengerId())
                                        .scheduleId(request.getScheduleId())
                                        .expiresAt(LocalDateTime.now().plusMinutes(SESSION_TTL_MINUTES))
                                        .build();
                        session = sessionRepository.save(session);
                }

                SeatLock lock = SeatLock.builder()
                                .seat(seat)
                                .scheduleId(request.getScheduleId())
                                .session(session)
                                .build();

                SeatLockDTO result;
                try {
                        result = toDTO(lockRepository.saveAndFlush(lock));
                } catch (DataIntegrityViolationException e) {
                        throw new SeatLockConflictException(
                                        "Seat " + seatId + " is already locked for schedule "
                                                        + request.getScheduleId() + ".");
                }

                socketService.broadcast(SeatUpdateMessage.builder()
                                .event(SeatUpdateMessage.Event.LOCKED)
                                .busId(seat.getBusId())
                                .seatId(seatId)
                                .seatNo(seat.getSeatNo())
                                .scheduleId(request.getScheduleId())
                                .timestamp(LocalDateTime.now())
                                .build());

                return result;
        }

        /**
         * Release a specific lock by seat and schedule.
         */
        @Transactional
        public void releaseLock(Long seatId, Long scheduleId) {
                SeatLock lock = lockRepository.findBySeatIdAndScheduleId(seatId, scheduleId)
                                .orElseThrow(() -> new SeatLockNotFoundException(seatId, null));

                Seat seat = lock.getSeat();
                lockRepository.delete(lock);

                socketService.broadcast(SeatUpdateMessage.builder()
                                .event(SeatUpdateMessage.Event.UNLOCKED)
                                .busId(seat.getBusId())
                                .seatId(seatId)
                                .seatNo(seat.getSeatNo())
                                .scheduleId(scheduleId)
                                .timestamp(LocalDateTime.now())
                                .build());
        }

        /**
         * Cancel a seat booking (soft delete — status set to CANCELLED).
         */
        @Transactional
        public BookingDTO cancelBooking(Long seatStatusId, Long passengerId) {
                Booking booking = bookingRepository.findById(seatStatusId)
                                .orElseThrow(() -> new BookingNotFoundException(seatStatusId));

                if (!booking.getPassengerId().equals(passengerId)) {
                        throw new SeatLockConflictException(
                                        "Passenger " + passengerId + " does not own booking " + seatStatusId + ".");
                }

                if (booking.getStatus() == BookingStatus.CANCELLED) {
                        return toBookingDTO(booking);
                }

                booking.setStatus(BookingStatus.CANCELLED);
                Booking saved = bookingRepository.save(booking);

                Seat seat = booking.getSeat();
                socketService.broadcast(SeatUpdateMessage.builder()
                                .event(SeatUpdateMessage.Event.CANCELLED)
                                .busId(seat.getBusId())
                                .seatId(seat.getSeatId())
                                .seatNo(seat.getSeatNo())
                                .scheduleId(booking.getScheduleId())
                                .fromStopOrder(booking.getFromStopOrder())
                                .toStopOrder(booking.getToStopOrder())
                                .timestamp(LocalDateTime.now())
                                .build());

                return toBookingDTO(saved);
        }

        /**
         * Get the current lock on a seat for a given schedule.
         */
        @Transactional(readOnly = true)
        public SeatLockDTO getLockBySeat(Long seatId, Long scheduleId) {
                return lockRepository.findBySeatIdAndScheduleId(seatId, scheduleId)
                                .map(this::toDTO)
                                .orElseThrow(() -> new SeatLockNotFoundException(seatId, null));
        }

        /**
         * Get all locks for a booking session.
         */
        @Transactional(readOnly = true)
        public List<SeatLockDTO> getLocksBySession(Long sessionId) {
                return lockRepository.findBySessionIdWithSeat(sessionId)
                                .stream()
                                .map(this::toDTO)
                                .toList();
        }

        /**
         * Get session info by ID.
         */
        @Transactional(readOnly = true)
        public BookingSessionDTO getSession(Long sessionId) {
                BookingSession session = sessionRepository.findById(sessionId)
                                .orElseThrow(() -> new SessionNotFoundException(sessionId));
                return toSessionDTO(session);
        }

        /**
         * Get booking history for a passenger.
         */
        @Transactional(readOnly = true)
        public List<BookingDTO> getBookingHistory(Long passengerId) {
                return bookingRepository.findByPassengerId(passengerId)
                                .stream()
                                .map(this::toBookingDTO)
                                .toList();
        }

        private SeatLockDTO toDTO(SeatLock lock) {
                return SeatLockDTO.builder()
                                .lockId(lock.getLockId())
                                .seatId(lock.getSeat().getSeatId())
                                .seatNo(lock.getSeat().getSeatNo())
                                .busId(lock.getSeat().getBusId())
                                .scheduleId(lock.getScheduleId())
                                .sessionId(lock.getSession().getSessionId())
                                .build();
        }

        private BookingDTO toBookingDTO(Booking booking) {
                return BookingDTO.builder()
                                .seatStatusId(booking.getSeatStatusId())
                                .seatId(booking.getSeat().getSeatId())
                                .seatNo(booking.getSeat().getSeatNo())
                                .busId(booking.getSeat().getBusId())
                                .scheduleId(booking.getScheduleId())
                                .passengerId(booking.getPassengerId())
                                .fromStopOrder(booking.getFromStopOrder())
                                .toStopOrder(booking.getToStopOrder())
                                .status(booking.getStatus())
                                .idempotencyKey(booking.getIdempotencyKey())
                                .createdAt(booking.getCreatedAt())
                                .updatedAt(booking.getUpdatedAt())
                                .build();
        }

        private BookingSessionDTO toSessionDTO(BookingSession session) {
                return BookingSessionDTO.builder()
                                .sessionId(session.getSessionId())
                                .passengerId(session.getPassengerId())
                                .scheduleId(session.getScheduleId())
                                .expiresAt(session.getExpiresAt())
                                .createdAt(session.getCreatedAt())
                                .build();
        }
}
