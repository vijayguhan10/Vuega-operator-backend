package net.vuega.vuega_backend.DTO.scheduler.route_stops;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRouteStopRequest {

    @NotNull(message = "Route ID is required")
    @Positive(message = "Route ID must be positive")
    private Long routeId;

    @NotNull(message = "City ID is required")
    private String cityId;

    @NotNull(message = "Stop order is required")
    @Positive(message = "Stop order must be positive")
    private Integer stopOrder;

    @NotNull(message = "Distance from source is required")
    @Positive(message = "Distance from source must be non-negative")
    private Integer distanceFromSource;

    @NotNull(message = "Departure offset is required")
    @Positive(message = "Departure offset must be non-negative")
    private Integer departureOffset;
}
