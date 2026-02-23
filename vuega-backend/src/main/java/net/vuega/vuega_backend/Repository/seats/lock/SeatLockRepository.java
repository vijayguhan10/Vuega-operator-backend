package net.vuega.vuega_backend.Repository.seats.lock;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import net.vuega.vuega_backend.Model.seats.lock.SeatLock;

// JPA repository for SeatLock — write-locked queries and bulk-delete for expiry.
@Repository
public interface SeatLockRepository extends JpaRepository<SeatLock, Long> {

    // ─── READ-ONLY (no lock — safe for GET endpoints) ────────────────────────────

    @Query("SELECT sl FROM SeatLock sl WHERE sl.seat.seatId = :seatId")
    Optional<SeatLock> findBySeat_SeatId(@Param("seatId") Long seatId);

    boolean existsBySeat_SeatId(Long seatId);

    // ─── PESSIMISTIC WRITE (used in all write operations) ────────────────────────

    // Used in acquireLock — blocks concurrent lock acquisition on same seat
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT sl FROM SeatLock sl WHERE sl.seat.seatId = :seatId")
    Optional<SeatLock> findBySeatIdForWrite(@Param("seatId") Long seatId);

    // Used in releaseLock and bookSeat — ensures exclusive access to lock record
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT sl FROM SeatLock sl WHERE sl.seat.seatId = :seatId AND sl.partnerId = :partnerId")
    Optional<SeatLock> findBySeatIdAndPartnerIdForWrite(
            @Param("seatId") Long seatId,
            @Param("partnerId") Long partnerId);

    // ─── SCHEDULER ───────────────────────────────────────────────────────────────

    List<SeatLock> findByExpiresAtBefore(LocalDateTime now);

    @Modifying
    @Query("DELETE FROM SeatLock sl WHERE sl.expiresAt < :now")
    int deleteExpiredLocks(@Param("now") LocalDateTime now);
}
