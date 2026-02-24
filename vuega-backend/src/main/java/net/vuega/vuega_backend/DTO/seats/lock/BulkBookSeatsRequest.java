package net.vuega.vuega_backend.DTO.seats.lock;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

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

    @NotNull(message = "seatIds is required")
    @Size(min = 1, max = 50, message = "seatIds must have between 1 and 50 entries")
    private List<Long> seatIds;

    // optional — per-seat idempotency key becomes "{prefix}:{seatId}"
    @Size(max = 30, message = "idempotencyKeyPrefix must be at most 30 characters")
    private String idempotencyKeyPrefix;
}
