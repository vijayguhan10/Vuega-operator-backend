package net.vuega.vuega_backend.Exception;

public class BookingNotFoundException extends RuntimeException {

    public BookingNotFoundException(Long seatStatusId) {
        super("Booking not found with id: " + seatStatusId);
    }
}
