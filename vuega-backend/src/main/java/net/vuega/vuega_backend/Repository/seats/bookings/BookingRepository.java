package net.vuega.vuega_backend.Repository.seats.bookings;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import net.vuega.vuega_backend.Model.seats.bookings.Booking;
import net.vuega.vuega_backend.Model.seats.bookings.BookingStatus;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

        @Query("""
                        SELECT COUNT(b) FROM Booking b
                        WHERE b.seat.seatId = :seatId
                        AND b.scheduleId = :scheduleId
                        AND b.status = :status
                        AND b.fromStopOrder < :toStop
                        AND b.toStopOrder > :fromStop
                        """)
        long countOverlappingBookings(
                        @Param("seatId") Long seatId,
                        @Param("scheduleId") Long scheduleId,
                        @Param("fromStop") int fromStop,
                        @Param("toStop") int toStop,
                        @Param("status") BookingStatus status);

        Optional<Booking> findByIdempotencyKey(String idempotencyKey);

        List<Booking> findByPassengerId(Long passengerId);

        @Query("""
                        SELECT b FROM Booking b
                        WHERE b.seat.seatId = :seatId
                        AND b.scheduleId = :scheduleId
                        ORDER BY b.createdAt DESC
                        """)
        List<Booking> findHistoryBySeatAndSchedule(
                        @Param("seatId") Long seatId,
                        @Param("scheduleId") Long scheduleId);
}
