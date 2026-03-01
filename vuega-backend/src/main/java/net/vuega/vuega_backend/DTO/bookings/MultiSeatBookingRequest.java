package net.vuega.vuega_backend.DTO.bookings;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class MultiSeatBookingRequest {

    @NotNull(message = "sessionId is required")
    private Long sessionId;

    @NotEmpty(message = "seatIds must not be empty")
    private List<Long> seatIds;

    @NotNull(message = "fromStopOrder is required")
    @Min(value = 0, message = "fromStopOrder must be >= 0")
    private Integer fromStopOrder;

    @NotNull(message = "toStopOrder is required")
    @Min(value = 1, message = "toStopOrder must be >= 1")
    private Integer toStopOrder;

    @NotEmpty(message = "passengerDetails must not be empty")
    @Valid
    private List<PassengerRequest> passengerDetails;

    @Size(max = 64, message = "idempotencyKey must be at most 64 characters")
    private String idempotencyKey;
}
