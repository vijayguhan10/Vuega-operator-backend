package net.vuega.vuega_backend.exception;

// Thrown when a conflicting lock exists or the requesting partner does not own the lock.
public class SeatLockConflictException extends RuntimeException {
    public SeatLockConflictException(String message) {
        super(message);
    }
}
