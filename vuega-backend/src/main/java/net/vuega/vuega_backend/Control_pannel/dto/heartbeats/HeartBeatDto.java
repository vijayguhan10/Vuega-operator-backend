package net.vuega.vuega_backend.Control_pannel.dto.heartbeats;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HeartBeatDto {

    private Long heartbeatId;

    @NotNull(message = "Operator ID is required")
    private Long operatorId;

    @NotNull(message = "Bus count is required")
    @Min(value = 0, message = "Bus count cannot be negative")
    private Integer busCount;

    @NotNull(message = "Route count is required")
    @Min(value = 0, message = "Route count cannot be negative")
    private Integer routeCount;

    @NotNull(message = "Trip count is required")
    @Min(value = 0, message = "Trip count cannot be negative")
    private Integer tripCount;
}