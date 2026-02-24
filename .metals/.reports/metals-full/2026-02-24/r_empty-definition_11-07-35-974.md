error id: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Service/seats/lock/SeatLockService.java:_empty_/SeatLockRepository#findActiveLock#
file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Service/seats/lock/SeatLockService.java
empty definition using pc, found symbol in pc: _empty_/SeatLockRepository#findActiveLock#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 5662
uri: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Service/seats/lock/SeatLockService.java
text:
```scala
package net.vuega.vuega_backend.Service.seats.lock;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.vuega.vuega_backend.DTO.bookings.BookingDTO;
import net.vuega.vuega_backend.DTO.seats.lock.AcquireLockRequest;
import net.vuega.vuega_backend.DTO.seats.lock.BookSeatRequest;
import net.vuega.vuega_backend.DTO.seats.lock.SeatLockDTO;
import net.vuega.vuega_backend.DTO.seats.socket.SeatUpdateMessage;
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
import net.vuega.vuega_backend.Service.seats.socket.SeatSocketService;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatLockService {

        private static final int LOCK_TTL_MINUTES = 10;

        private final SeatRepository seatRepository;
        private final SeatLockRepository lockRepository;
        private final BookingRepository bookingRepository;
        private final SeatSocketService socketService;

        @Transactional
        public SeatLockDTO acquireLock(Long seatId, AcquireLockRequest request) {
                if (request.getFromStopOrder() >= request.getToStopOrder()) {
                        throw new InvalidStopRangeException(
                                        "fromStopOrder must be less than toStopOrder");
                }

                Seat seat = seatRepository.findById(seatId)
                                .orElseThrow(() -> new SeatNotFoundException(seatId));

                long overlappingBookings = bookingRepository.countOverlappingBookings(
                                seatId, request.getScheduleId(),
                                request.getFromStopOrder(), request.getToStopOrder(),
                                BookingStatus.CONFIRMED);
                if (overlappingBookings > 0) {
                        throw new SeatNotAvailableException(
                                        "Seat " + seatId + " is already booked for schedule "
                                                        + request.getScheduleId()
                                                        + " on this segment.");
                }

                List<SeatLock> overlappingLocks = lockRepository.findOverlappingActiveLocks(
                                seatId, request.getScheduleId(),
                                request.getFromStopOrder(), request.getToStopOrder(),
                                LocalDateTime.now());
                if (!overlappingLocks.isEmpty()) {
                        SeatLock existing = overlappingLocks.get(0);
                        throw new SeatLockConflictException(
                                        "Seat " + seatId + " is already locked by partner " + existing.getPartnerId()
                                                        + " for this segment. Lock expires at "
                                                        + existing.getExpiresAt() + ".");
                }

                SeatLock lock = SeatLock.builder()
                                .seat(seat)
                                .scheduleId(request.getScheduleId())
                                .partnerId(request.getPartnerId())
                                .fromStopOrder(request.getFromStopOrder())
                                .toStopOrder(request.getToStopOrder())
                                .expiresAt(LocalDateTime.now().plusMinutes(LOCK_TTL_MINUTES))
                                .build();

                SeatLockDTO result;
                try {
                        result = toDTO(lockRepository.saveAndFlush(lock));
                } catch (DataIntegrityViolationException e) {
                        throw new SeatLockConflictException(
                                        "Seat " + seatId + " was just locked by another request. Please try again.");
                }

                socketService.broadcast(SeatUpdateMessage.builder()
                                .event(SeatUpdateMessage.Event.LOCKED)
                                .busId(seat.getBusId())
                                .seatId(seatId)
                                .seatNo(seat.getSeatNo())
                                .scheduleId(request.getScheduleId())
                                .fromStopOrder(request.getFromStopOrder())
                                .toStopOrder(request.getToStopOrder())
                                .timestamp(LocalDateTime.now())
                                .build());

                return result;
        }

        @Transactional
        public void releaseLock(Long seatId, Long scheduleId, Long partnerId, int fromStop, int toStop) {
                SeatLock lock = lockRepository.@@findActiveLock(seatId, scheduleId, partnerId, fromStop, toStop)
                                .orElseThrow(() -> new SeatLockNotFoundException(seatId, partnerId));

                Seat seat = lock.getSeat();
                lockRepository.delete(lock);

                socketService.broadcast(SeatUpdateMessage.builder()
                                .event(SeatUpdateMessage.Event.UNLOCKED)
                                .busId(seat.getBusId())
                                .seatId(seatId)
                                .seatNo(seat.getSeatNo())
                                .scheduleId(scheduleId)
                                .fromStopOrder(fromStop)
                                .toStopOrder(toStop)
                                .timestamp(LocalDateTime.now())
                                .build());
        }

        @Transactional
        public BookingDTO bookSeat(Long seatId, BookSeatRequest request) {
                if (request.getFromStopOrder() >= request.getToStopOrder()) {
                        throw new InvalidStopRangeException(
                                        "fromStopOrder must be less than toStopOrder");
                }

                SeatLock lock = lockRepository.findActiveLock(
                                seatId, request.getScheduleId(), request.getPartnerId(),
                                request.getFromStopOrder(), request.getToStopOrder())
                                .orElseThrow(() -> new SeatLockConflictException(
                                                "No active lock found for seat " + seatId
                                                                + " on schedule " + request.getScheduleId()
                                                                + ". Please acquire a lock first via POST /api/seats/"
                                                                + seatId + "/lock."));

                if (lock.getExpiresAt().isBefore(LocalDateTime.now())) {
                        lockRepository.delete(lock);
                        throw new SeatNotAvailableException(
                                        "Lock on seat " + seatId + " expired at " + lock.getExpiresAt()
                                                        + ". Please acquire a new lock.");
                }

                long overlapping = bookingRepository.countOverlappingBookings(
                                seatId, request.getScheduleId(),
                                request.getFromStopOrder(), request.getToStopOrder(),
                                BookingStatus.CONFIRMED);
                if (overlapping > 0) {
                        throw new SeatNotAvailableException(
                                        "Seat " + seatId + " is already booked for this segment.");
                }

                Seat seat = lock.getSeat();

                Booking booking = Booking.builder()
                                .seat(seat)
                                .scheduleId(request.getScheduleId())
                                .userId(request.getUserId())
                                .partnerId(request.getPartnerId())
                                .fromStopOrder(request.getFromStopOrder())
                                .toStopOrder(request.getToStopOrder())
                                .status(BookingStatus.CONFIRMED)
                                .bookedAt(LocalDateTime.now())
                                .build();

                Booking saved;
                try {
                        saved = bookingRepository.saveAndFlush(booking);
                } catch (DataIntegrityViolationException e) {
                        throw new SeatNotAvailableException(
                                        "Seat " + seatId + " was just booked by another request for this segment.");
                }

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

        @Transactional(readOnly = true)
        public SeatLockDTO getLockBySeat(Long seatId) {
                return lockRepository.findActiveLockBySeatId(seatId, LocalDateTime.now())
                                .map(this::toDTO)
                                .orElseThrow(() -> new SeatLockNotFoundException(seatId, null));
        }

        @Scheduled(fixedRate = 10_000)
        @Transactional
        public void releaseExpiredLocks() {
                LocalDateTime now = LocalDateTime.now();
                List<SeatLock> expired = lockRepository.findExpiredLocksWithSeat(now);
                if (!expired.isEmpty()) {
                        int count = lockRepository.deleteExpiredLocks(now);
                        expired.forEach(lock -> socketService.broadcast(SeatUpdateMessage.builder()
                                        .event(SeatUpdateMessage.Event.EXPIRED)
                                        .busId(lock.getSeat().getBusId())
                                        .seatId(lock.getSeat().getSeatId())
                                        .seatNo(lock.getSeat().getSeatNo())
                                        .scheduleId(lock.getScheduleId())
                                        .fromStopOrder(lock.getFromStopOrder())
                                        .toStopOrder(lock.getToStopOrder())
                                        .timestamp(now)
                                        .build()));
                        log.info("[SeatLockService] Released {} expired lock(s)", count);
                }
        }

        private SeatLockDTO toDTO(SeatLock lock) {
                return SeatLockDTO.builder()
                                .lockId(lock.getLockId())
                                .seatId(lock.getSeat().getSeatId())
                                .seatNo(lock.getSeat().getSeatNo())
                                .busId(lock.getSeat().getBusId())
                                .scheduleId(lock.getScheduleId())
                                .partnerId(lock.getPartnerId())
                                .fromStopOrder(lock.getFromStopOrder())
                                .toStopOrder(lock.getToStopOrder())
                                .expiresAt(lock.getExpiresAt())
                                .build();
        }

        private BookingDTO toBookingDTO(Booking booking) {
                return BookingDTO.builder()
                                .bookingId(booking.getBookingId())
                                .seatId(booking.getSeat().getSeatId())
                                .seatNo(booking.getSeat().getSeatNo())
                                .busId(booking.getSeat().getBusId())
                                .scheduleId(booking.getScheduleId())
                                .userId(booking.getUserId())
                                .partnerId(booking.getPartnerId())
                                .fromStopOrder(booking.getFromStopOrder())
                                .toStopOrder(booking.getToStopOrder())
                                .status(booking.getStatus())
                                .bookedAt(booking.getBookedAt())
                                .build();
        }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/SeatLockRepository#findActiveLock#