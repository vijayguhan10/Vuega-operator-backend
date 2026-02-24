package net.vuega.vuega_backend.DTO.bookings;

import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * Top-level response for the bulk-book endpoint.
 *
 * Clients should inspect {@code results} to see which seats
 * were booked and which failed (and why), rather than relying
 * only on the HTTP status code.
 */
@Data
@Builder
public class BulkBookingSummaryDTO {

    /** Number of seat IDs that were submitted. */
    private int totalRequested;

    /** Number of seats that were successfully booked. */
    private int totalBooked;

    /** Number of seats that could not be booked (conflict, already booked, not found, etc.). */
    private int totalFailed;

    /** Per-seat outcomes in the same order as the request's seat ID list. */
    private List<SeatBookingResult> results;
}
