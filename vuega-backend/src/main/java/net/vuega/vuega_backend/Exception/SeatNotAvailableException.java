package net.vuega.vuega_backend.Exception;

// Thrown when a seat's current status prevents the requested operation.
public class SeatNotAvailableException extends RuntimeException {
    public SeatNotAvailableException(String message) {
        super(message);
    }
}
