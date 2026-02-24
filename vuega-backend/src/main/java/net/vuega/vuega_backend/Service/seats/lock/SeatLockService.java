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
    private final SeatSocketService socketService;
    private final SeatService seatService;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public SeatLockDTO acquireLock(Long seatId, AcquireLockRequest request) {
        Seat seat = seatRepository.findByIdWithPessimisticLock(seatId)
                .orElseThrow(() -> new SeatNotFoundException(seatId));

        if (seat.getStatus() == SeatStatus.NOT_AVAILABLE) {
            throw new SeatNotAvailableException(
                    "Seat " + seatId + " is NOT_AVAILABLE (already booked) and cannot be locked.");
        }

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

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public SeatDTO bookSeat(Long seatId, Long partnerId) {
        Seat seat = seatRepository.findByIdWithPessimisticLock(seatId)
                .orElseThrow(() -> new SeatNotFoundException(seatId));

        if (seat.getStatus() == SeatStatus.NOT_AVAILABLE) {
            throw new SeatNotAvailableException("Seat " + seatId + " is already booked.");
        }

        // Lock the lock row — prevents concurrent releaseLock or duplicate bookSeat
        SeatLock lock = lockRepository.findBySeatIdAndPartnerIdForWrite(seatId, partnerId)
                .orElseThrow(() -> new SeatLockConflictException(
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
public SeatLockDTO getLockBySeat(Long seatId) {
        return lockRepository.findBySeat_SeatId(seatId)
                .map(this::toDTO)
                .orElseThrow(() -> new SeatLockNotFoundException(seatId, null));
    }

    // ─── SCHEDULED: RELEASE EXPIRED LOCKS ───────────────────────────────────────

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

                .lockId(lock.getLockId())
                .seatId(lock.getSeat().getSeatId())
                .seatNo(lock.getSeat().getSeatNo())
                .busId(lock.getSeat().getBusId())
                .partnerId(lock.getPartnerId())
                .expiresAt(lock.getExpiresAt())
                .build();
    }
}
