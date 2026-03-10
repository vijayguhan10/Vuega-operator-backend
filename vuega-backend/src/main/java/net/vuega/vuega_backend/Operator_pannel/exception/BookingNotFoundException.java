package net.vuega.vuega_backend.Operator_pannel.exception;

public class BookingNotFoundException extends RuntimeException {

    public BookingNotFoundException(Long id) {
        super("Booking not found with id: " + id);
    }

    public BookingNotFoundException(String message) {
        super(message);
    }
}
