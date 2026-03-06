package net.vuega.vuega_backend.Operator_pannel.exception;

// Thrown when a seat's current status prevents the requested operation.
public class SeatNotAvailableException extends RuntimeException {
    public SeatNotAvailableException(String message) {
        super(message);
    }
}
