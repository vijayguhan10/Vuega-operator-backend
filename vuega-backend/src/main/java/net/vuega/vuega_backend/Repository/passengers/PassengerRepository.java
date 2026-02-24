package net.vuega.vuega_backend.Repository.passengers;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.vuega.vuega_backend.Model.passengers.Passenger;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {

    List<Passenger> findByBookingBookingId(Long bookingId);
}
