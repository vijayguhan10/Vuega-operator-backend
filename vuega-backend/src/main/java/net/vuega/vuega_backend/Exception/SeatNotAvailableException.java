package net.vuega.vuega_backend.Exception;

/**
 * Thrown when an operation requires a seat to have a specific status
 * (e.g., AVAILABLE or LOCKED) but the seat is in a different state.
 */
public class SeatNotAvailableException extends RuntimeException {
    public SeatNotAvailableException(String message) {
        super(message);
    }
}
