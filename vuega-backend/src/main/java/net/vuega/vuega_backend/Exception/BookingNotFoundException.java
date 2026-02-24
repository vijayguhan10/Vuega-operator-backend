package net.vuega.vuega_backend.Exception;

public class BookingNotFoundException extends RuntimeException {

    public BookingNotFoundException(Long id) {
        super("Booking not found with id: " + id);
    }

    public BookingNotFoundException(String message) {
        super(message);
    }
}
