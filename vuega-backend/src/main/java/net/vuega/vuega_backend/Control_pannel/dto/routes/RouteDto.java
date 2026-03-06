package net.vuega.vuega_backend.Control_pannel.dto.routes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.vuega.vuega_backend.Control_pannel.util.RouteStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteDto {

    private Long routeId;
    private Long operatorId;
    private Long fromCityId;
    private Long toCityId;
    private Integer totalDistance;
    private RouteStatus status;
}
