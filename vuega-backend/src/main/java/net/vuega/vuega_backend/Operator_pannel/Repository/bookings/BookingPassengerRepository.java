package net.vuega.vuega_backend.Repository.bookings;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.vuega.vuega_backend.Model.bookings.BookingPassenger;

@Repository
public interface BookingPassengerRepository extends JpaRepository<BookingPassenger, Long> {

    List<BookingPassenger> findByBookingId(Long bookingId);

    List<BookingPassenger> findByPassengerId(Long passengerId);
}
