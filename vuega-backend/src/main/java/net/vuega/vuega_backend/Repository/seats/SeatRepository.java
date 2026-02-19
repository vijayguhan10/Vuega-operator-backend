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

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    // ---- Basic finders -----------------------------------------------

    List<Seat> findByBusId(Long busId);

    List<Seat> findByBusIdAndStatus(Long busId, SeatStatus status);

    /**
     * Used to enforce the (bus_id, seat_no) uniqueness check at the service layer.
     */
    boolean existsByBusIdAndSeatNo(Long busId, String seatNo);

    // ---- Availability ------------------------------------------------

    /**
     * Returns all AVAILABLE seats for a bus.
     * Because each seat row carries exactly one active segment at a time,
     * status = AVAILABLE fully represents "no current booking".
     */
    @Query("SELECT s FROM Seat s WHERE s.busId = :busId AND s.status = 'AVAILABLE'")
    List<Seat> findAvailableSeatsByBusId(@Param("busId") Long busId);

    // ---- Pessimistic locking -----------------------------------------

    /**
     * Acquires a PESSIMISTIC_WRITE (SELECT … FOR UPDATE) lock on the row
     * identified by seatId.
     *
     * Effect: all other transactions that call this method (or any write on
     * the same row) will block until the current transaction commits or rolls
     * back, preventing two concurrent requests from both reading the seat as
     * AVAILABLE and both successfully locking it.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.seatId = :seatId")
    Optional<Seat> findByIdWithPessimisticLock(@Param("seatId") Long seatId);

    /**
     * Batch pessimistic lock — ORDER BY seatId ensures a consistent lock
     * acquisition order across concurrent transactions to avoid deadlocks
     * when multiple seats are locked in one request.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.seatId IN :seatIds ORDER BY s.seatId ASC")
    List<Seat> findAllByIdWithPessimisticLock(@Param("seatIds") List<Long> seatIds);

    // ---- Expired lock cleanup ----------------------------------------

    /**
     * Returns all LOCKED seats whose lock timestamp is older than expiryTime.
     * Used by the scheduled cleanup job to log/audit released seats before
     * the bulk UPDATE.
     */
    @Query("SELECT s FROM Seat s WHERE s.status = 'LOCKED' AND s.lockedAt < :expiryTime")
    List<Seat> findExpiredLockedSeats(@Param("expiryTime") LocalDateTime expiryTime);

    /**
     * Single bulk UPDATE to release all expired locks — far more efficient
     * than loading entities one by one.
     *
     * @Modifying is required for any JPQL UPDATE/DELETE to signal Spring Data
     *            that the query mutates state and clears the first-level cache.
     */
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
