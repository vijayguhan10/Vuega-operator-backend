package net.vuega.vuega_backend.DTO.seats.lock;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AcquireLockRequest {

    @NotNull(message = "partnerId is required")
    private Long partnerId;

    @NotNull(message = "scheduleId is required")
    private Long scheduleId;
}
