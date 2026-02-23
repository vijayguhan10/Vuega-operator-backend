package net.vuega.vuega_backend.DTO.seats.lock;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

// Request DTO for releasing a seat lock.
@Data
public class ReleaseLockRequest {
    @NotNull(message = "partnerId is required")
    private Long partnerId;
}
