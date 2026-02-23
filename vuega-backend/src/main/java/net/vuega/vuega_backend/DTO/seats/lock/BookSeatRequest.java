package net.vuega.vuega_backend.DTO.seats.lock;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

// Request DTO for confirming a seat booking.
@Data
public class BookSeatRequest {
    @NotNull(message = "partnerId is required")
    private Long partnerId;
}
