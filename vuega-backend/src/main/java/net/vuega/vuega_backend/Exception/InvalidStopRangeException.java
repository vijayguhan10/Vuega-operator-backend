package net.vuega.vuega_backend.Exception;

// Thrown when fromStopOrder is not strictly less than toStopOrder.
public class InvalidStopRangeException extends RuntimeException {
    public InvalidStopRangeException(String message) {
        super(message);
    }
}
