package net.vuega.vuega_backend.Repository.seats;

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
import net.vuega.vuega_backend.Model.seats.Seat;
import net.vuega.vuega_backend.Model.seats.SeatStatus;

// Seat JPA repository — finders, pessimistic lock, and bulk expiry.
@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByBusId(Long busId);

    List<Seat> findByBusIdAndStatus(Long busId, SeatStatus status);

    boolean existsByBusIdAndSeatNo(Long busId, String seatNo);

    @Query("SELECT s FROM Seat s WHERE s.busId = :busId AND s.status = 'AVAILABLE'")
    List<Seat> findAvailableSeatsByBusId(@Param("busId") Long busId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.seatId = :seatId")
    Optional<Seat> findByIdWithPessimisticLock(@Param("seatId") Long seatId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.seatId IN :seatIds ORDER BY s.seatId ASC")
    List<Seat> findAllByIdWithPessimisticLock(@Param("seatIds") List<Long> seatIds);

    @Query("SELECT s FROM Seat s WHERE s.status = 'LOCKED' AND s.lockedAt < :expiryTime")
    List<Seat> findExpiredLockedSeats(@Param("expiryTime") LocalDateTime expiryTime);

    @Modifying
    @Query("""
            UPDATE Seat s
            SET    s.status        = 'AVAILABLE',
                   s.lockedBy      = null,
                   s.lockedAt      = null,
                   s.fromStopOrder = null,
                   s.toStopOrder   = null
            WHERE  s.status = 'LOCKED'
              AND  s.lockedAt < :expiryTime
            """)
    int bulkReleaseExpiredLocks(@Param("expiryTime") LocalDateTime expiryTime);
}
