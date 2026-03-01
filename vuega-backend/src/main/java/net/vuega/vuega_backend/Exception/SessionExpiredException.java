package net.vuega.vuega_backend.Exception;

public class SessionExpiredException extends RuntimeException {
    public SessionExpiredException(String message) {
        super(message);
    }
}
