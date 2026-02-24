package net.vuega.vuega_backend.DTO.bookings;

import lombok.Builder;
import lombok.Data;

/**
 * Per-seat outcome inside a bulk-booking response.
 */
@Data
@Builder
public class SeatBookingResult {

    public enum Status {
        BOOKED,
        FAILED
    }

    private Long seatId;

    /**
     * Human-readable seat number (e.g. "A3") — null when the seat was not found.
     */
    private String seatNo;

    private Status status;

    /** Populated on success. */
    private BookingDTO booking;

    /** Populated on failure — contains the reason the seat could not be booked. */
    private String reason;

    // -------------------------------------------------------------------------
    // Factory helpers
    // -------------------------------------------------------------------------

    public static SeatBookingResult success(Long seatId, BookingDTO booking) {
        return SeatBookingResult.builder()
                .seatId(seatId)
                .seatNo(booking.getSeatNo())
                .status(Status.BOOKED)
                .booking(booking)
                .build();
    }

    public static SeatBookingResult failure(Long seatId, String reason) {
        return SeatBookingResult.builder()
                .seatId(seatId)
                .status(Status.FAILED)
                .reason(reason)
                .build();
    }
}
