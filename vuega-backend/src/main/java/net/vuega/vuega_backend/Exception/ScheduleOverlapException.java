package net.vuega.vuega_backend.Exception;

public class ScheduleOverlapException extends RuntimeException {
    public ScheduleOverlapException(String message) {
        super(message);
    }
}