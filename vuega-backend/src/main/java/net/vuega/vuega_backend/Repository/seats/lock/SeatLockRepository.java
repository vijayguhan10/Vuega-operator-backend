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
            SELECT sl FROM SeatLock sl
            WHERE sl.seat.seatId = :seatId
            AND sl.scheduleId = :scheduleId
            AND sl.expiresAt > :now
            AND sl.fromStopOrder < :toStop
            AND sl.toStopOrder > :fromStop
            """)
    List<SeatLock> findOverlappingActiveLocks(
            @Param("seatId") Long seatId,
            @Param("scheduleId") Long scheduleId,
            @Param("fromStop") int fromStop,
            @Param("toStop") int toStop,
            @Param("now") LocalDateTime now);

    @Query("""
            SELECT sl FROM SeatLock sl
            WHERE sl.seat.seatId = :seatId
            AND sl.scheduleId = :scheduleId
            AND sl.partnerId = :partnerId
            AND sl.fromStopOrder = :fromStop
            AND sl.toStopOrder = :toStop
            """)
    Optional<SeatLock> findActiveLock(
            @Param("seatId") Long seatId,
            @Param("scheduleId") Long scheduleId,
            @Param("partnerId") Long partnerId,
            @Param("fromStop") int fromStop,
            @Param("toStop") int toStop);

    List<SeatLock> findByExpiresAtBefore(LocalDateTime now);

    @Modifying
    @Query("DELETE FROM SeatLock sl WHERE sl.expiresAt < :now")
    int deleteExpiredLocks(@Param("now") LocalDateTime now);
}
