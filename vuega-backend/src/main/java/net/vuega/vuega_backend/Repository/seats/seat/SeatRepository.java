package net.vuega.vuega_backend.Repository.seats.seat;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import net.vuega.vuega_backend.Model.seats.seat.Seat;

// JPA repository for Seat — includes pessimistic-lock queries used by the lock service.
@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByBusId(Long busId);

    boolean existsByBusIdAndSeatNo(Long busId, String seatNo);

    @Query("SELECT s FROM Seat s WHERE s.busId = :busId AND s.status = 'AVAILABLE'")
    List<Seat> findAvailableSeatsByBusId(@Param("busId") Long busId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.seatId = :seatId")
    Optional<Seat> findByIdWithPessimisticLock(@Param("seatId") Long seatId);
}
