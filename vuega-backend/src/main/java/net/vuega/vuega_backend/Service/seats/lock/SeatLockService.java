package net.vuega.vuega_backend.Service.seats.lock;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.vuega.vuega_backend.DTO.bookings.BookingDTO;
import net.vuega.vuega_backend.DTO.bookings.BulkBookingSummaryDTO;
import net.vuega.vuega_backend.DTO.bookings.SeatBookingResult;
import net.vuega.vuega_backend.DTO.seats.lock.AcquireLockRequest;
import net.vuega.vuega_backend.DTO.seats.lock.BookSeatRequest;
import net.vuega.vuega_backend.DTO.seats.lock.BulkBookSeatsRequest;
import net.vuega.vuega_backend.DTO.seats.lock.SeatLockDTO;
import net.vuega.vuega_backend.DTO.seats.socket.SeatUpdateMessage;
import net.vuega.vuega_backend.Exception.BookingNotFoundException;
import net.vuega.vuega_backend.Exception.InvalidStopRangeException;
import net.vuega.vuega_backend.Exception.SeatLockConflictException;
import net.vuega.vuega_backend.Exception.SeatLockNotFoundException;
import net.vuega.vuega_backend.Exception.SeatNotAvailableException;
import net.vuega.vuega_backend.Exception.SeatNotFoundException;
import net.vuega.vuega_backend.Model.bookings.Booking;
import net.vuega.vuega_backend.Model.bookings.BookingStatus;
import net.vuega.vuega_backend.Model.seats.lock.SeatLock;
import net.vuega.vuega_backend.Model.seats.seat.Seat;
import net.vuega.vuega_backend.Repository.bookings.BookingRepository;
import net.vuega.vuega_backend.Repository.seats.lock.SeatLockRepository;
import net.vuega.vuega_backend.Repository.seats.seat.SeatRepository;
import net.vuega.vuega_backend.Service.redis.RedisLockService;
import net.vuega.vuega_backend.Service.seats.socket.SeatSocketService;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatLockService {

        private static final int LOCK_TTL_MINUTES = 10;
        private static final long LOCK_TTL_SECONDS = LOCK_TTL_MINUTES * 60L;

        private final SeatRepository seatRepository;
        private final SeatLockRepository lockRepository;
        private final BookingRepository bookingRepository;
        private final SeatSocketService socketService;
        private final RedisLockService redisLockService;

        // -------------------------------------------------------------------------
        // Acquire lock
        // -------------------------------------------------------------------------

        @Transactional
        public SeatLockDTO acquireLock(Long seatId, AcquireLockRequest request) {
                Seat seat = seatRepository.findById(seatId)
                                .orElseThrow(() -> new SeatNotFoundException(seatId));

                // Try Redis SETNX first — fast-fail without a DB round-trip
                boolean redisAcquired = redisLockService.acquireSeatLock(
                                request.getScheduleId(), seatId, request.getPartnerId(), LOCK_TTL_SECONDS);
                if (!redisAcquired) {
                        String holder = redisLockService.getLockHolder(
                                        redisLockService.buildKey(request.getScheduleId(), seatId));
                        throw new SeatLockConflictException(
                                        "Seat " + seatId + " is already locked (holder=" + holder + ") for schedule "
                                                        + request.getScheduleId() + ".");
                }

                SeatLock lock = SeatLock.builder()
                                .seat(seat)
                                .scheduleId(request.getScheduleId())
                                .partnerId(request.getPartnerId())
                                .expiresAt(LocalDateTime.now().plusMinutes(LOCK_TTL_MINUTES))
                                .build();

                SeatLockDTO result;
                try {
                        result = toDTO(lockRepository.saveAndFlush(lock));
                } catch (DataIntegrityViolationException e) {
                        // DB says the seat is already locked — roll back Redis key we just took
                        redisLockService.releaseSeatLock(request.getScheduleId(), seatId, request.getPartnerId());
                        SeatLock existing = lockRepository
                                        .findActiveLockBySeatId(seatId, request.getScheduleId(), LocalDateTime.now())
                                        .orElse(null);
                        String detail = existing != null
                                        ? "partner " + existing.getPartnerId() + ", expires at "
                                                        + existing.getExpiresAt()
                                        : "another request";
                        throw new SeatLockConflictException(
                                        "Seat " + seatId + " is already locked by " + detail + ".");
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

        // -------------------------------------------------------------------------
        // Release lock
        // -------------------------------------------------------------------------

        @Transactional
        public void releaseLock(Long seatId, Long scheduleId, Long partnerId) {
                SeatLock lock = lockRepository.findActiveLock(seatId, scheduleId, partnerId, LocalDateTime.now())
                                .orElseThrow(() -> new SeatLockNotFoundException(seatId, partnerId));

                Seat seat = lock.getSeat();

                // Release Redis key (best-effort — DB record is source of truth)
                redisLockService.releaseSeatLock(scheduleId, seatId, partnerId);

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

        // -------------------------------------------------------------------------
        // Renew lock
        // -------------------------------------------------------------------------

        @Transactional
        public SeatLockDTO renewLock(Long seatId, Long scheduleId, Long partnerId) {
                SeatLock lock = lockRepository.findActiveLock(seatId, scheduleId, partnerId, LocalDateTime.now())
                                .orElseThrow(() -> new SeatLockNotFoundException(seatId, partnerId));

                // Extend Redis TTL (best-effort; re-acquire if key drifted)
                boolean renewed = redisLockService.renewSeatLock(scheduleId, seatId, partnerId, LOCK_TTL_SECONDS);
                if (!renewed) {
                        log.warn("renewLock: Redis key absent/wrong-owner for seatId={} scheduleId={} partnerId={}",
                                        seatId, scheduleId, partnerId);
                        redisLockService.acquireSeatLock(scheduleId, seatId, partnerId, LOCK_TTL_SECONDS);
                }

                // Extend DB expiry
                lock.setExpiresAt(LocalDateTime.now().plusMinutes(LOCK_TTL_MINUTES));
                return toDTO(lockRepository.save(lock));
        }

        // -------------------------------------------------------------------------
        // Book seat
        // -------------------------------------------------------------------------

        @Transactional
        public BookingDTO bookSeat(Long seatId, BookSeatRequest request) {
                if (request.getFromStopOrder() >= request.getToStopOrder()) {
                        throw new InvalidStopRangeException("fromStopOrder must be less than toStopOrder");
                }

                // --- Idempotency check ---
                if (request.getIdempotencyKey() != null && !request.getIdempotencyKey().isBlank()) {
                        return bookingRepository.findByIdempotencyKey(request.getIdempotencyKey())
                                        .map(existing -> {
                                                log.info("bookSeat: idempotency hit key={}",
                                                                request.getIdempotencyKey());
                                                return toBookingDTO(existing);
                                        })
                                        .orElseGet(() -> doBookSeat(seatId, request));
                }

                return doBookSeat(seatId, request);
        }

        private BookingDTO doBookSeat(Long seatId, BookSeatRequest request) {
                SeatLock lock = lockRepository.findActiveLock(
                                seatId, request.getScheduleId(), request.getPartnerId(), LocalDateTime.now())
                                .orElseThrow(() -> new SeatLockConflictException(
                                                "No active lock found for seat " + seatId + " on schedule "
                                                                + request.getScheduleId()
                                                                + ". Please acquire a lock first via POST /api/seats/"
                                                                + seatId + "/lock."));

                long overlapping = bookingRepository.countOverlappingBookings(
                                seatId, request.getScheduleId(),
                                request.getFromStopOrder(), request.getToStopOrder(),
                                BookingStatus.BOOKED);
                if (overlapping > 0) {
                        throw new SeatNotAvailableException("Seat " + seatId + " is already booked for this segment.");
                }

                Seat seat = lock.getSeat();

                Booking booking = Booking.builder()
                                .seat(seat)
                                .scheduleId(request.getScheduleId())
                                .partnerId(request.getPartnerId())
                                .fromStopOrder(request.getFromStopOrder())
                                .toStopOrder(request.getToStopOrder())
                                .status(BookingStatus.BOOKED)
                                .idempotencyKey(request.getIdempotencyKey())
                                .build();

                Booking saved;
                try {
                        saved = bookingRepository.saveAndFlush(booking);
                } catch (DataIntegrityViolationException e) {
                        throw new SeatNotAvailableException(
                                        "Seat " + seatId + " was just booked by another request for this segment.");
                }

                // Release Redis + DB lock after successful booking
                redisLockService.releaseSeatLock(request.getScheduleId(), seatId, request.getPartnerId());
                lockRepository.delete(lock);

                socketService.broadcast(SeatUpdateMessage.builder()
                                .event(SeatUpdateMessage.Event.BOOKED)
                                .busId(seat.getBusId())
                                .seatId(seatId)
                                .seatNo(seat.getSeatNo())
                                .scheduleId(request.getScheduleId())
                                .fromStopOrder(request.getFromStopOrder())
                                .toStopOrder(request.getToStopOrder())
                                .timestamp(LocalDateTime.now())
                                .build());

                return toBookingDTO(saved);
        }

        // -------------------------------------------------------------------------
        // Cancel booking (soft-delete — status set to CANCELLED, record kept)
        // -------------------------------------------------------------------------

        @Transactional
        public BookingDTO cancelBooking(Long seatStatusId, Long partnerId) {
                Booking booking = bookingRepository.findById(seatStatusId)
                                .orElseThrow(() -> new BookingNotFoundException(seatStatusId));

                if (!booking.getPartnerId().equals(partnerId)) {
                        throw new SeatLockConflictException(
                                        "Partner " + partnerId + " does not own booking " + seatStatusId + ".");
                }

                if (booking.getStatus() == BookingStatus.CANCELLED) {
                        // Already cancelled — idempotent
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

        // -------------------------------------------------------------------------
        // Query
        // -------------------------------------------------------------------------

        @Transactional(readOnly = true)
        public SeatLockDTO getLockBySeat(Long seatId, Long scheduleId) {
                return lockRepository.findActiveLockBySeatId(seatId, scheduleId, LocalDateTime.now())
                                .map(this::toDTO)
                                .orElseThrow(() -> new SeatLockNotFoundException(seatId, null));
        }

        @Transactional(readOnly = true)
        public List<BookingDTO> getBookingHistory(Long partnerId) {
                return bookingRepository.findByPartnerId(partnerId)
                                .stream()
                                .map(this::toBookingDTO)
                                .toList();
        }

        // -------------------------------------------------------------------------
        // Bulk book multiple seats in one request
        // -------------------------------------------------------------------------

        /**
         * Books multiple seats for a single partner/schedule/segment in one call.
         *
         * Each seat is processed independently: a failure on one seat does not
         * prevent the remaining seats from being attempted. The caller should
         * inspect {@link BulkBookingSummaryDTO#getResults()} for per-seat outcomes.
         *
         * Seat selection modes:
         * Explicit list — populate {@code request.seatIds} (up to 50 entries).
         * Consecutive ID range — populate {@code request.fromSeatId} and
         * {@code request.toSeatId}; every seat whose primary-key ID falls
         * within [fromSeatId, toSeatId] will be attempted.
         */
        public BulkBookingSummaryDTO bookMultipleSeats(BulkBookSeatsRequest request) {
                if (request.getFromStopOrder() >= request.getToStopOrder()) {
                        throw new InvalidStopRangeException("fromStopOrder must be less than toStopOrder");
                }

                // Resolve the seat ID list from whichever selection mode was used
                List<Long> ids;
                if (request.getSeatIds() != null && !request.getSeatIds().isEmpty()) {
                        ids = request.getSeatIds();
                } else {
                        ids = seatRepository.findSeatIdsByIdRange(
                                        request.getFromSeatId(), request.getToSeatId());
                }

                List<SeatBookingResult> results = new ArrayList<>(ids.size());

                for (Long seatId : ids) {
                        // Build a standard single-seat BookSeatRequest for each seat
                        BookSeatRequest bookReq = new BookSeatRequest();
                        bookReq.setPartnerId(request.getPartnerId());
                        bookReq.setScheduleId(request.getScheduleId());
                        bookReq.setFromStopOrder(request.getFromStopOrder());
                        bookReq.setToStopOrder(request.getToStopOrder());
                        if (request.getIdempotencyKeyPrefix() != null
                                        && !request.getIdempotencyKeyPrefix().isBlank()) {
                                // Per-seat idempotency key = prefix + ":" + seatId
                                bookReq.setIdempotencyKey(
                                                request.getIdempotencyKeyPrefix() + ":" + seatId);
                        }

                        try {
                                BookingDTO booking = lockAndBookSeat(seatId, bookReq);
                                results.add(SeatBookingResult.success(seatId, booking));
                        } catch (Exception e) {
                                results.add(SeatBookingResult.failure(seatId, e.getMessage()));
                        }
                }

                long booked = results.stream()
                                .filter(r -> r.getStatus() == SeatBookingResult.Status.BOOKED)
                                .count();
                return BulkBookingSummaryDTO.builder()
                                .totalRequested(ids.size())
                                .totalBooked((int) booked)
                                .totalFailed((int) (ids.size() - booked))
                                .results(results)
                                .build();
        }

        /**
         * Acquires a Redis lock, validates no overlapping bookings exist, persists
         * the booking, releases the lock, and broadcasts the BOOKED event — all for
         * a single seat.
         *
         * Used by {@link #bookMultipleSeats} so that each seat within a bulk
         * request succeeds or fails independently.
         *
         * This method is intentionally NOT annotated @Transactional. Each DB
         * operation auto-commits; the DataIntegrityViolationException catch on
         * saveAndFlush provides the same concurrent-write safety net as the
         * regular single-seat bookSeat path.
         */
        private BookingDTO lockAndBookSeat(Long seatId, BookSeatRequest request) {
                // --- Idempotency check ---
                if (request.getIdempotencyKey() != null
                                && !request.getIdempotencyKey().isBlank()) {
                        var existing = bookingRepository
                                        .findByIdempotencyKey(request.getIdempotencyKey());
                        if (existing.isPresent()) {
                                log.info("lockAndBookSeat: idempotency hit key={}",
                                                request.getIdempotencyKey());
                                return toBookingDTO(existing.get());
                        }
                }

                Seat seat = seatRepository.findById(seatId)
                                .orElseThrow(() -> new SeatNotFoundException(seatId));

                // --- Acquire Redis lock (fast-fail without a DB round-trip) ---
                boolean redisAcquired = redisLockService.acquireSeatLock(
                                request.getScheduleId(), seatId, request.getPartnerId(),
                                LOCK_TTL_SECONDS);
                if (!redisAcquired) {
                        String holder = redisLockService.getLockHolder(
                                        redisLockService.buildKey(request.getScheduleId(), seatId));
                        throw new SeatLockConflictException(
                                        "Seat " + seatId + " is already locked (holder=" + holder
                                                        + ") for schedule " + request.getScheduleId() + ".");
                }

                // --- Check for overlapping confirmed bookings ---
                long overlapping = bookingRepository.countOverlappingBookings(
                                seatId, request.getScheduleId(),
                                request.getFromStopOrder(), request.getToStopOrder(),
                                BookingStatus.BOOKED);
                if (overlapping > 0) {
                        redisLockService.releaseSeatLock(
                                        request.getScheduleId(), seatId, request.getPartnerId());
                        throw new SeatNotAvailableException(
                                        "Seat " + seatId + " is already booked for this segment.");
                }

                // --- Persist the booking ---
                Booking booking = Booking.builder()
                                .seat(seat)
                                .scheduleId(request.getScheduleId())
                                .partnerId(request.getPartnerId())
                                .fromStopOrder(request.getFromStopOrder())
                                .toStopOrder(request.getToStopOrder())
                                .status(BookingStatus.BOOKED)
                                .idempotencyKey(request.getIdempotencyKey())
                                .build();

                Booking saved;
                try {
                        saved = bookingRepository.saveAndFlush(booking);
                } catch (DataIntegrityViolationException e) {
                        redisLockService.releaseSeatLock(
                                        request.getScheduleId(), seatId, request.getPartnerId());
                        throw new SeatNotAvailableException(
                                        "Seat " + seatId
                                                        + " was just booked by a concurrent request for this segment.");
                }

                // --- Release lock ---
                redisLockService.releaseSeatLock(
                                request.getScheduleId(), seatId, request.getPartnerId());

                // --- Broadcast ---
                socketService.broadcast(SeatUpdateMessage.builder()
                                .event(SeatUpdateMessage.Event.BOOKED)
                                .busId(seat.getBusId())
                                .seatId(seatId)
                                .seatNo(seat.getSeatNo())
                                .scheduleId(request.getScheduleId())
                                .fromStopOrder(request.getFromStopOrder())
                                .toStopOrder(request.getToStopOrder())
                                .timestamp(LocalDateTime.now())
                                .build());

                return toBookingDTO(saved);
        }

        // -------------------------------------------------------------------------
        // Scheduled cleanup
        // -------------------------------------------------------------------------

        @Scheduled(fixedRate = 10_000)
        @Transactional
        public void releaseExpiredLocks() {
                LocalDateTime now = LocalDateTime.now();
                List<SeatLock> expired = lockRepository.findExpiredLocksWithSeat(now);
                if (!expired.isEmpty()) {
                        // Clean up Redis keys before removing DB records
                        expired.forEach(lock -> redisLockService.releaseSeatLock(
                                        lock.getScheduleId(), lock.getSeat().getSeatId(), lock.getPartnerId()));

                        int count = lockRepository.deleteExpiredLocks(now);

                        expired.forEach(lock -> socketService.broadcast(SeatUpdateMessage.builder()
                                        .event(SeatUpdateMessage.Event.EXPIRED)
                                        .busId(lock.getSeat().getBusId())
                                        .seatId(lock.getSeat().getSeatId())
                                        .seatNo(lock.getSeat().getSeatNo())
                                        .scheduleId(lock.getScheduleId())
                                        .timestamp(now)
                                        .build()));

                        log.info("[SeatLockService] Released {} expired lock(s)", count);
                }
        }

        // -------------------------------------------------------------------------
        // Mappers
        // -------------------------------------------------------------------------

        private SeatLockDTO toDTO(SeatLock lock) {
                return SeatLockDTO.builder()
                                .lockId(lock.getLockId())
                                .seatId(lock.getSeat().getSeatId())
                                .seatNo(lock.getSeat().getSeatNo())
                                .busId(lock.getSeat().getBusId())
                                .scheduleId(lock.getScheduleId())
                                .partnerId(lock.getPartnerId())
                                .expiresAt(lock.getExpiresAt())
                                .build();
        }

        private BookingDTO toBookingDTO(Booking booking) {
                return BookingDTO.builder()
                                .seatStatusId(booking.getSeatStatusId())
                                .seatId(booking.getSeat().getSeatId())
                                .seatNo(booking.getSeat().getSeatNo())
                                .busId(booking.getSeat().getBusId())
                                .scheduleId(booking.getScheduleId())
                                .partnerId(booking.getPartnerId())
                                .fromStopOrder(booking.getFromStopOrder())
                                .toStopOrder(booking.getToStopOrder())
                                .status(booking.getStatus())
                                .idempotencyKey(booking.getIdempotencyKey())
                                .createdAt(booking.getCreatedAt())
                                .updatedAt(booking.getUpdatedAt())
                                .build();
        }
}
