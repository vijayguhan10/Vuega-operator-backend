package net.vuega.vuega_backend.DTO.seats.lock;

import java.util.List;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request body for the bulk-book endpoint.
 *
 * Supports two mutually exclusive seat selection modes:
 *
 *   1.  Explicit list  — provide a non-empty {@code seatIds} list.
 *       e.g. "seatIds": [1, 5, 6, 7]
 *
 *   2.  Consecutive ID range — provide both {@code fromSeatId} and
 *       {@code toSeatId}.  All seat records whose primary-key ID falls
 *       in [fromSeatId, toSeatId] (inclusive) will be booked.
 *       e.g. "fromSeatId": 1, "toSeatId": 12
 *
 * Exactly one mode must be used per request.
 */
@Data
public class BulkBookSeatsRequest {

    @NotNull(message = "partnerId is required")
    private Long partnerId;

    @NotNull(message = "scheduleId is required")
    private Long scheduleId;

    @NotNull(message = "fromStopOrder is required")
    @Min(value = 0, message = "fromStopOrder must be >= 0")
    private Integer fromStopOrder;

    @NotNull(message = "toStopOrder is required")
    @Min(value = 1, message = "toStopOrder must be >= 1")
    private Integer toStopOrder;

    // ----- Mode A: explicit seat IDs -----

    @Size(min = 1, max = 50, message = "seatIds must have between 1 and 50 entries when provided")
    private List<Long> seatIds;

    // ----- Mode B: consecutive seat-ID range -----

    private Long fromSeatId;

    private Long toSeatId;

    /**
     * Optional idempotency key prefix.
     * When set, each per-seat booking is submitted with the key
     * "{idempotencyKeyPrefix}:{seatId}", making the whole batch
     * safely retriable.
     */
    @Size(max = 30, message = "idempotencyKeyPrefix must be at most 30 characters")
    private String idempotencyKeyPrefix;

    // ----- Cross-field validation -----

    @AssertTrue(message = "Provide either a non-empty seatIds list OR both fromSeatId and toSeatId (not both, not neither)")
    public boolean isSeatSelectionValid() {
        boolean hasExplicitList = seatIds != null && !seatIds.isEmpty();
        boolean hasRange = fromSeatId != null && toSeatId != null;
        return hasExplicitList ^ hasRange; // exactly one must be true
    }

    @AssertTrue(message = "fromSeatId must be less than or equal to toSeatId")
    public boolean isSeatRangeValid() {
        if (fromSeatId == null || toSeatId == null) {
            return true; // range not in use — skip this check
        }
        return fromSeatId <= toSeatId;
    }
}
