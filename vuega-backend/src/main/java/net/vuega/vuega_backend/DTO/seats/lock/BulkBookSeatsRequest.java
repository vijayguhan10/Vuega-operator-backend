package net.vuega.vuega_backend.DTO.seats.lock;

import java.util.List;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Bulk seat booking request.
 * Use seatIds for an explicit list (e.g. [1,5,6,9])
 * or fromSeatId + toSeatId for a consecutive range (e.g. 1..12).
 * Exactly one mode must be provided.
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

    // explicit seat IDs: [1, 5, 6, 9]
    @Size(min = 1, max = 50, message = "seatIds must have between 1 and 50 entries when provided")
    private List<Long> seatIds;

    // consecutive range: every seat ID in [fromSeatId, toSeatId]
    private Long fromSeatId;
    private Long toSeatId;

    // optional — per-seat idempotency key becomes "{prefix}:{seatId}"
    @Size(max = 30, message = "idempotencyKeyPrefix must be at most 30 characters")
    private String idempotencyKeyPrefix;

    @AssertTrue(message = "Provide either a non-empty seatIds list OR both fromSeatId and toSeatId (not both, not neither)")
    public boolean isSeatSelectionValid() {
        boolean hasExplicitList = seatIds != null && !seatIds.isEmpty();
        boolean hasRange = fromSeatId != null && toSeatId != null;
        return hasExplicitList ^ hasRange;
    }

    @AssertTrue(message = "fromSeatId must be less than or equal to toSeatId")
    public boolean isSeatRangeValid() {
        if (fromSeatId == null || toSeatId == null)
            return true;
        return fromSeatId <= toSeatId;
    }
}
