package net.vuega.vuega_backend.Exception;

/**
 * Thrown when a seat lock cannot be acquired or released because another
 * session holds it, or when the booking request comes from a different
 * session than the one that locked the seat.
 */
public class SeatLockConflictException extends RuntimeException {
    public SeatLockConflictException(String message) {
        super(message);
    }
}
