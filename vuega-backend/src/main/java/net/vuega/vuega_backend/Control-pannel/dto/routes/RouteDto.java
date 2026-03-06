package net.vuega.control_plane.dto.routes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.vuega.control_plane.util.RouteStatus;

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
