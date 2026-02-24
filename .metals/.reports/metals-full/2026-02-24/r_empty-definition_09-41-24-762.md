error id: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Service/seats/lock/SeatLockService.java:_empty_/SeatLockConflictException#
file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Service/seats/lock/SeatLockService.java
empty definition using pc, found symbol in pc: _empty_/SeatLockConflictException#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 2434
uri: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Service/seats/lock/SeatLockService.java
text:
```scala
package net.vuega.vuega_backend.Service.seats.lock;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.vuega.vuega_backend.DTO.seats.lock.AcquireLockRequest;
import net.vuega.vuega_backend.DTO.seats.lock.SeatLockDTO;
import net.vuega.vuega_backend.DTO.seats.seat.SeatDTO;
import net.vuega.vuega_backend.DTO.seats.socket.SeatUpdateMessage;
import net.vuega.vuega_backend.Exception.SeatLockConflictException;
import net.vuega.vuega_backend.Exception.SeatLockNotFoundException;
import net.vuega.vuega_backend.Exception.SeatNotAvailableException;
import net.vuega.vuega_backend.Exception.SeatNotFoundException;
import net.vuega.vuega_backend.Model.seats.lock.SeatLock;
import net.vuega.vuega_backend.Model.seats.seat.Seat;
import net.vuega.vuega_backend.Model.seats.seat.SeatStatus;
import net.vuega.vuega_backend.Repository.seats.lock.SeatLockRepository;
import net.vuega.vuega_backend.Repository.seats.seat.SeatRepository;
import net.vuega.vuega_backend.Service.seats.seat.SeatService;
import net.vuega.vuega_backend.Service.seats.socket.SeatSocketService;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatLockService {

        private static final int LOCK_TTL_MINUTES = 10;

        private final SeatRepository seatRepository;
        private final SeatLockRepository lockRepository;
        private final SeatService seatService;
        private final SeatSocketService socketService;

        @Transactional(isolation = Isolation.SERIALIZABLE)
        public SeatLockDTO acquireLock(Long seatId, AcquireLockRequest request) {
                Seat seat = seatRepository.findByIdWithPessimisticLock(seatId)
                                .orElseThrow(() -> new SeatNotFoundException(seatId));

                if (seat.getStatus() == SeatStatus.NOT_AVAILABLE) {
                        throw new SeatNotAvailableException(
                                        "Seat " + seatId + " is NOT_AVAILABLE (already booked) and cannot be locked.");
                }

                lockRepository.findBySeatIdForWrite(seatId).ifPresent(existing -> {
                        throw new SeatLockConflictE@@xception(
                                        "Seat " + seatId + " is already locked by partner " + existing.getPartnerId()
                                                        + ". Lock expires at " + existing.getExpiresAt() + ".");
                });

                SeatLock lock = SeatLock.builder()
                                .seat(seat)
                                .partnerId(request.getPartnerId())
                                .expiresAt(LocalDateTime.now().plusMinutes(LOCK_TTL_MINUTES))
                                .build();

                SeatLockDTO result = toDTO(lockRepository.save(lock));

                socketService.broadcast(SeatUpdateMessage.builder()
                                .event(SeatUpdateMessage.Event.LOCKED)
                                .busId(seat.getBusId())
                                .seatId(seatId)
                                .seatNo(seat.getSeatNo())
                                .status(seat.getStatus())
                                .timestamp(LocalDateTime.now())
                                .build());

                return result;
        }

        @Transactional(isolation = Isolation.READ_COMMITTED)
        public void releaseLock(Long seatId, Long partnerId) {
                SeatLock lock = lockRepository.findBySeatIdAndPartnerIdForWrite(seatId, partnerId)
                                .orElseThrow(() -> new SeatLockNotFoundException(seatId, partnerId));

                Seat seat = lock.getSeat();
                lockRepository.delete(lock);

                socketService.broadcast(SeatUpdateMessage.builder()
                                .event(SeatUpdateMessage.Event.UNLOCKED)
                                .busId(seat.getBusId())
                                .seatId(seatId)
                                .seatNo(seat.getSeatNo())
                                .status(seat.getStatus())
                                .timestamp(LocalDateTime.now())
                                .build());
        }

        @Transactional(isolation = Isolation.READ_COMMITTED)
        public SeatDTO bookSeat(Long seatId, Long partnerId) {
                Seat seat = seatRepository.findByIdWithPessimisticLock(seatId)
                                .orElseThrow(() -> new SeatNotFoundException(seatId));

                if (seat.getStatus() == SeatStatus.NOT_AVAILABLE) {
                        throw new SeatNotAvailableException("Seat " + seatId + " is already booked.");
                }

                SeatLock lock = lockRepository.findBySeatIdAndPartnerIdForWrite(seatId, partnerId)
                                .orElseThrow(() -> new SeatLockConflictException(
                                                "Lock not found for seat " + seatId
                                                                + ". Please acquire a lock first via POST /api/seats/"
                                                                + seatId
                                                                + "/lock."));

                if (lock.getExpiresAt().isBefore(LocalDateTime.now())) {
                        lockRepository.delete(lock);
                        throw new SeatNotAvailableException(
                                        "Lock on seat " + seatId + " expired at " + lock.getExpiresAt()
                                                        + ". Please acquire a new lock.");
                }

                seat.setStatus(SeatStatus.NOT_AVAILABLE);
                seatRepository.save(seat);
                lockRepository.delete(lock);

                socketService.broadcast(SeatUpdateMessage.builder()
                                .event(SeatUpdateMessage.Event.BOOKED)
                                .busId(seat.getBusId())
                                .seatId(seatId)
                                .seatNo(seat.getSeatNo())
                                .status(SeatStatus.NOT_AVAILABLE)
                                .timestamp(LocalDateTime.now())
                                .build());

                return seatService.toDTO(seat);
        }

        @Transactional(readOnly = true)
        public SeatLockDTO getLockBySeat(Long seatId) {
                return lockRepository.findBySeat_SeatId(seatId)
                                .map(this::toDTO)
                                .orElseThrow(() -> new SeatLockNotFoundException(seatId, null));
        }

        @Scheduled(fixedRate = 10_000)
        @Transactional
        public void releaseExpiredLocks() {
                LocalDateTime now = LocalDateTime.now();
                List<SeatLock> expired = lockRepository.findByExpiresAtBefore(now);
                if (!expired.isEmpty()) {
                        expired.forEach(lock -> {
                                socketService.broadcast(SeatUpdateMessage.builder()
                                                .event(SeatUpdateMessage.Event.UNLOCKED)
                                                .busId(lock.getSeat().getBusId())
                                                .seatId(lock.getSeat().getSeatId())
                                                .seatNo(lock.getSeat().getSeatNo())
                                                .status(lock.getSeat().getStatus())
                                                .timestamp(LocalDateTime.now())
                                                .build());
                        });
                        int count = lockRepository.deleteExpiredLocks(now);
                        log.info("[SeatLockService] Released {} expired lock(s)", count);
                }
        }

        private SeatLockDTO toDTO(SeatLock lock) {
                return SeatLockDTO.builder()
                                .lockId(lock.getLockId())
                                .seatId(lock.getSeat().getSeatId())
                                .seatNo(lock.getSeat().getSeatNo())
                                .busId(lock.getSeat().getBusId())
                                .partnerId(lock.getPartnerId())
                                .expiresAt(lock.getExpiresAt())
                                .build();
        }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/SeatLockConflictException#