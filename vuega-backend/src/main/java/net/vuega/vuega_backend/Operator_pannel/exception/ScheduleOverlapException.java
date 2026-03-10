package net.vuega.vuega_backend.Operator_pannel.exception;

public class ScheduleOverlapException extends RuntimeException {
    public ScheduleOverlapException(String message) {
        super(message);
    }
}