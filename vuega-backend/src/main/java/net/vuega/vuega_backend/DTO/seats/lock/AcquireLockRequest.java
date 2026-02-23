package net.vuega.vuega_backend.DTO.seats.lock;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

// Request DTO for acquiring a seat lock.
@Data
public class AcquireLockRequest {
    @NotNull(message = "partnerId is required")
    private Long partnerId;
}
