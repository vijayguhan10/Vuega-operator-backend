package net.vuega.vuega_backend.Exception;

/**
 * Thrown when fromStopOrder >= toStopOrder, making the journey segment invalid.
 */
public class InvalidStopRangeException extends RuntimeException {
    public InvalidStopRangeException(String message) {
        super(message);
    }
}
