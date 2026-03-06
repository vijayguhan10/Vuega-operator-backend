package net.vuega.vuega_backend.Operator_pannel.DTO.scheduler.route_stops;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateRouteStopRequest {

    private String cityId;

    @Positive(message = "Stop order must be positive")
    private Integer stopOrder;

    @Positive(message = "Distance from source must be non-negative")
    private Integer distanceFromSource;

    @Positive(message = "Departure offset must be non-negative")
    private Integer departureOffset;
}
