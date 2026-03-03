package net.vuega.vuega_backend.Repository.seats.bookings;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import net.vuega.vuega_backend.Model.seats.bookings.Booking;
import net.vuega.vuega_backend.Model.seats.bookings.BookingStatus;

@Repository
public interface SeatBookingRepository extends JpaRepository<Booking, Long> {

        @Query("""
                        SELECT COUNT(b) FROM SeatBooking b
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

        List<Booking> findByPassengerId(Long passengerId);

        List<Booking> findByBookingId(Long bookingId);

        @Query("""
                        SELECT b FROM SeatBooking b
                        WHERE b.seat.seatId = :seatId
                        AND b.scheduleId = :scheduleId
                        """)
        List<Booking> findHistoryBySeatAndSchedule(
                        @Param("seatId") Long seatId,
                        @Param("scheduleId") Long scheduleId);
}
