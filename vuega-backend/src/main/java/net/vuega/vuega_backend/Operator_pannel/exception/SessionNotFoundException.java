package net.vuega.vuega_backend.Operator_pannel.exception;

public class SessionNotFoundException extends RuntimeException {
    public SessionNotFoundException(Long sessionId) {
        super("Booking session not found with id: " + sessionId);
    }
}
