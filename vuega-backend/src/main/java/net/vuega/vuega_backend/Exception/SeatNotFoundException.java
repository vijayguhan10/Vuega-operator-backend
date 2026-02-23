package net.vuega.vuega_backend.Exception;

// Thrown when no seat exists for the given seatId.
public class SeatNotFoundException extends RuntimeException {
    public SeatNotFoundException(Long seatId) {
        super("Seat not found with id: " + seatId);
    }
}
