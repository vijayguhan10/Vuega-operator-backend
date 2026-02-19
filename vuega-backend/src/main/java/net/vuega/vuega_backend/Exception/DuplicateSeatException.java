package net.vuega.vuega_backend.Exception;

/**
 * Thrown when a seat with the same (bus_id, seat_no) combination already
 * exists.
 */
public class DuplicateSeatException extends RuntimeException {
    public DuplicateSeatException(String busId, String seatNo) {
        super("Seat number '" + seatNo + "' already exists on bus " + busId);
    }
}
