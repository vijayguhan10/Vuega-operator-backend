package net.vuega.vuega_backend.DTO.seats.lock;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReleaseLockRequest {

    @NotNull(message = "partnerId is required")
    private Long partnerId;
}
