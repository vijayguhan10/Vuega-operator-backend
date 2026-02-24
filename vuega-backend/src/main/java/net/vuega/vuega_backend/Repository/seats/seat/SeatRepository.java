package net.vuega.vuega_backend.Repository.seats.seat;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import net.vuega.vuega_backend.Model.seats.seat.Seat;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByBusId(Long busId);

    boolean existsByBusIdAndSeatNo(Long busId, String seatNo);

    @Query("SELECT s FROM Seat s WHERE s.busId = :busId AND s.status = 'AVAILABLE'")
    List<Seat> findAvailableSeatsByBusId(@Param("busId") Long busId);

    @Modifying
    @Query("UPDATE Seat s SET s.status = 'NOT_AVAILABLE' WHERE s.seatId = :seatId AND s.status = 'AVAILABLE'")
    int bookIfAvailable(@Param("seatId") Long seatId);
}
