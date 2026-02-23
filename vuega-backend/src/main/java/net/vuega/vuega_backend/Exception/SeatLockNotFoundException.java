package net.vuega.vuega_backend.Exception;

public class SeatLockNotFoundException extends RuntimeException {

    public SeatLockNotFoundException(Long lockId) {
        super("Seat lock not found with id: " + lockId);
    }

    public SeatLockNotFoundException(Long seatId, Long partnerId) {
        super("No active lock found for seat " + seatId + " by partner " + partnerId);
    }
}
