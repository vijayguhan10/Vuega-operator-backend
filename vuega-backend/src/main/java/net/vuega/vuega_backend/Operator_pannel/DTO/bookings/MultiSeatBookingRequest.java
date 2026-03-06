package net.vuega.vuega_backend.Operator_pannel.DTO.bookings;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MultiSeatBookingRequest {

    @NotNull(message = "sessionId is required")
    private Long sessionId;

    @NotNull(message = "partnerId is required")
    private Long partnerId;

    @NotEmpty(message = "passengerDetails must not be empty")
    @Valid
    private List<PassengerRequest> passengerDetails;

    @Size(max = 64, message = "idempotencyKey must be at most 64 characters")
    private String idempotencyKey;
}
