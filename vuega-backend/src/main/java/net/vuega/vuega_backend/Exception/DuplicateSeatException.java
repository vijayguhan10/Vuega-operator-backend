package net.vuega.vuega_backend.Exception;

// Thrown when the same seat number already exists on the given bus.
public class DuplicateSeatException extends RuntimeException {
    public DuplicateSeatException(String busId, String seatNo) {
        super("Seat number '" + seatNo + "' already exists on bus " + busId);
    }
}
