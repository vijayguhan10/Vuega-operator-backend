package net.vuega.vuega_backend.Repository.seats.lock;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import net.vuega.vuega_backend.Model.seats.lock.SeatLock;

@Repository
public interface SeatLockRepository extends JpaRepository<SeatLock, Long> {

    @Query("""
            SELECT l FROM SeatLock l
            WHERE l.seat.seatId = :seatId
            AND l.scheduleId = :scheduleId
            AND l.passengerId = :passengerId
            AND l.expiresAt > :now
            """)
    Optional<SeatLock> findActiveLock(
            @Param("seatId") Long seatId,
            @Param("scheduleId") Long scheduleId,
            @Param("passengerId") Long passengerId,
            @Param("now") LocalDateTime now);

    @Query("""
            SELECT l FROM SeatLock l
            WHERE l.seat.seatId = :seatId
            AND l.scheduleId = :scheduleId
            AND l.expiresAt > :now
            """)
    Optional<SeatLock> findActiveLockBySeatId(
            @Param("seatId") Long seatId,
            @Param("scheduleId") Long scheduleId,
            @Param("now") LocalDateTime now);

    @Query("SELECT l FROM SeatLock l JOIN FETCH l.seat WHERE l.expiresAt < :now")
    List<SeatLock> findExpiredLocksWithSeat(@Param("now") LocalDateTime now);

    @Modifying
    @Query("DELETE FROM SeatLock sl WHERE sl.expiresAt < :now")
    int deleteExpiredLocks(@Param("now") LocalDateTime now);
}
