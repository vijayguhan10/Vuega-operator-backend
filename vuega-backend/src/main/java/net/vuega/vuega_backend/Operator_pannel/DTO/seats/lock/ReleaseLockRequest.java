package net.vuega.vuega_backend.DTO.seats.lock;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReleaseLockRequest {

    @NotNull(message = "passengerId is required")
    private Long passengerId;

    @NotNull(message = "scheduleId is required")
    private Long scheduleId;
}
