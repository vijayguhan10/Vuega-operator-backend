package net.vuega.vuega_backend.Repository.seats.seat;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import net.vuega.vuega_backend.Model.seats.seat.Seat;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByBusId(Long busId);

    boolean existsByBusIdAndSeatNo(Long busId, String seatNo);

    @Query("""
            SELECT s FROM Seat s WHERE s.busId = :busId
            AND NOT EXISTS (
                SELECT 1 FROM Booking b
                WHERE b.seat.seatId = s.seatId
                AND b.scheduleId = :scheduleId
                AND b.status = 'CONFIRMED'
                AND b.fromStopOrder < :toStop
                AND b.toStopOrder > :fromStop
            )
            """)
    List<Seat> findAvailableSeatsForSegment(
            @Param("busId") Long busId,
            @Param("scheduleId") Long scheduleId,
            @Param("fromStop") int fromStop,
            @Param("toStop") int toStop);

}
