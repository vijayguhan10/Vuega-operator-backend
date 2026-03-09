package net.vuega.vuega_backend.Control_pannel.dto.routes;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.vuega.vuega_backend.Control_pannel.util.RouteStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteDto {

    private Long routeId;

    @NotNull(message = "Operator ID is required")
    private Long operatorId;

    @NotNull(message = "From city ID is required")
    private Long fromCityId;

    @NotNull(message = "To city ID is required")
    private Long toCityId;

    @NotNull(message = "Total distance is required")
    @Min(value = 1, message = "Total distance must be greater than 0")
    private Integer totalDistance;

    @NotNull(message = "Status is required")
    private RouteStatus status;
}
