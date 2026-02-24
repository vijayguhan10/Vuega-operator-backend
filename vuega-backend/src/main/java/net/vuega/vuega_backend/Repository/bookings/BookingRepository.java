package net.vuega.vuega_backend.Repository.bookings;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.vuega.vuega_backend.Model.bookings.Booking;
import net.vuega.vuega_backend.Model.bookings.BookingStatus;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByPnr(String pnr);

    List<Booking> findByPartnerId(Long partnerId);

    List<Booking> findByScheduleId(Long scheduleId);

    List<Booking> findByStatus(BookingStatus status);
}
