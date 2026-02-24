package net.vuega.vuega_backend.DTO.bookings;

import lombok.Builder;
import lombok.Data;

// Per-seat outcome inside a bulk-booking response.
@Data
@Builder
public class SeatBookingResult {

    public enum Status {
        BOOKED,
        FAILED
    }

    private Long seatId;
    // null when the seat was not found
    private String seatNo;

    private Status status;

    // populated on success
    private BookingDTO booking;

    // populated on failure — reason the seat could not be booked
    private String reason;

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
