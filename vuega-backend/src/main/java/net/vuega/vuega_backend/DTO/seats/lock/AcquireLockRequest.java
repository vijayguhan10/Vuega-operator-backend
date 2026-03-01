package net.vuega.vuega_backend.DTO.seats.lock;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AcquireLockRequest {

    @NotNull(message = "passengerId is required")
    private Long passengerId;

    @NotNull(message = "scheduleId is required")
    private Long scheduleId;

    /** If null, a new BookingSession will be created. */
    private Long sessionId;
}
