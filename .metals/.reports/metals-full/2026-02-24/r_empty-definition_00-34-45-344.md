error id: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Service/seats/lock/SeatLockService.java:net/vuega/vuega_backend/DTO/seats/lock/AcquireLockRequest#
file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Service/seats/lock/SeatLockService.java
empty definition using pc, found symbol in pc: net/vuega/vuega_backend/DTO/seats/lock/AcquireLockRequest#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 462
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
import net.vuega.vuega_backend.DTO.seats.lock.@@AcquireLockRequest;
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

// Handles all seat locking and booking logic using the separate seat_locks table.
@Service
@RequiredArgsConstructor
@Slf4j
public class SeatLockService {

    private static final int LOCK_TTL_MINUTES = 10;

    private final SeatRepository seatRepository;
    private final SeatLockRepository lockRepository;
    private final SeatSocketService socketService;
    private final SeatService seatService;

    // ─── ACQUIRE LOCK ────────────────────────────────────────────────────────────

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public SeatLockDTO acquireLock(Long seatId, AcquireLockRequest request) {
        // Lock seat row first, then check for existing lock — prevents TOCTOU race.
        Seat seat = seatRepository.findByIdWithPessimisticLock(seatId)
                .orElseThrow(() -> new SeatNotFoundException(seatId));

        if (seat.getStatus() == SeatStatus.NOT_AVAILABLE) {
            throw new SeatNotAvailableException(
                    "Seat " + seatId + " is NOT_AVAILABLE (already booked) and cannot be locked.");
        }

        // Lock the lock row (if it exists) — prevents double-lock race
        lockRepository.findBySeatIdForWrite(seatId).ifPresent(existing -> {
            throw new SeatLockConflictException(
                    "Seat " + seatId + " is already locked by partner " + existing.getPartnerId()
                            + ". Lock expires at " + existing.getExpiresAt() + ".");
        });

        SeatLock lock = SeatLock.builder()
                .seat(seat)
                .partnerId(request.getPartnerId())
                .expiresAt(LocalDateTime.now().plusMinutes(LOCK_TTL_MINUTES))
                .build();

        SeatLockDTO dto = toDTO(lockRepository.save(lock));

        socketService.broadcast(SeatUpdateMessage.builder()
                .event(SeatUpdateMessage.Event.LOCKED)
                .seatId(seatId)
                .busId(seat.getBusId())
                .seatNo(seat.getSeatNo())
                .status(seat.getStatus())
                .count(1)
                .timestamp(LocalDateTime.now())
                .build());

        return dto;
    }

    // ─── RELEASE LOCK ────────────────────────────────────────────────────────────

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void releaseLock(Long seatId, Long partnerId) {
        SeatLock lock = lockRepository.findBySeatIdAndPartnerIdForWrite(seatId, partnerId)
                .orElseThrow(() -> new SeatLockNotFoundException(seatId, partnerId));

        Seat seat = lock.getSeat();
        lockRepository.delete(lock);

        socketService.broadcast(SeatUpdateMessage.builder()
                .event(SeatUpdateMessage.Event.UNLOCKED)
                .seatId(seatId)
                .busId(seat.getBusId())
                .seatNo(seat.getSeatNo())
                .status(seat.getStatus())
                .count(1)
                .timestamp(LocalDateTime.now())
                .build());
    }

    // ─── BOOK SEAT ───────────────────────────────────────────────────────────────

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public SeatDTO bookSeat(Long seatId, Long partnerId) {
        // Seat row locked first — consistent ordering avoids deadlock with acquireLock.
        Seat seat = seatRepository.findByIdWithPessimisticLock(seatId)
                .orElseThrow(() -> new SeatNotFoundException(seatId));

        if (seat.getStatus() == SeatStatus.NOT_AVAILABLE) {
            throw new SeatNotAvailableException("Seat " + seatId + " is already booked.");
        }

        // Lock the lock row — prevents concurrent releaseLock or duplicate bookSeat
        SeatLock lock = lockRepository.findBySeatIdAndPartnerIdForWrite(seatId, partnerId)
                .orElseThrow(() -> new SeatLockConflictException(
                        "No active lock found for seat " + seatId + " by partner " + partnerId
                                + ". Please acquire a lock first via POST /api/seats/" + seatId + "/lock."));

        if (lock.getExpiresAt().isBefore(LocalDateTime.now())) {
            lockRepository.delete(lock);
            throw new SeatNotAvailableException(
                    "Lock on seat " + seatId + " expired at " + lock.getExpiresAt()
                            + ". Please acquire a new lock.");
        }

        seat.setStatus(SeatStatus.NOT_AVAILABLE);
        seatRepository.save(seat);
        lockRepository.delete(lock);

        SeatDTO dto = seatService.toDTO(seat);

        socketService.broadcast(SeatUpdateMessage.builder()
                .event(SeatUpdateMessage.Event.BOOKED)
                .seatId(dto.getSeatId())
                .busId(dto.getBusId())
                .seatNo(dto.getSeatNo())
                .status(dto.getStatus())
                .count(1)
                .timestamp(LocalDateTime.now())
                .build());

        return dto;
    }

    // ─── GET LOCK ────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public SeatLockDTO getLockBySeat(Long seatId) {
        return lockRepository.findBySeat_SeatId(seatId)
                .map(this::toDTO)
                .orElseThrow(() -> new SeatLockNotFoundException(seatId, null));
    }

    // ─── SCHEDULED: RELEASE EXPIRED LOCKS ───────────────────────────────────────

    @Scheduled(fixedRate = 10_000)
    @Transactional
    public void releaseExpiredLocks() {
        LocalDateTime now = LocalDateTime.now();
        List<SeatLock> expired = lockRepository.findByExpiresAtBefore(now);
        if (!expired.isEmpty()) {
            int count = lockRepository.deleteExpiredLocks(now);
            log.info("[SeatLockService] Bulk-released {} expired seat lock(s).", count);
            socketService.broadcast(SeatUpdateMessage.builder()
                    .event(SeatUpdateMessage.Event.EXPIRED)
                    .count(count)
                    .timestamp(now)
                    .build());
        }
    }

    // ─── MAPPER ──────────────────────────────────────────────────────────────────

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

empty definition using pc, found symbol in pc: net/vuega/vuega_backend/DTO/seats/lock/AcquireLockRequest#