package net.vuega.vuega_backend.Operator_pannel.DTO.scheduler.route_stops;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteStopDTO {

    private Long stopId;
    private Long routeId;
    private String cityId;
    private Integer stopOrder;
    private Integer distanceFromSource;
    private Integer departureOffset;
}
