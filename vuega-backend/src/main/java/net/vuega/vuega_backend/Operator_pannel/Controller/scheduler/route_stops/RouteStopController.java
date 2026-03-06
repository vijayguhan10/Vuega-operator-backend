package net.vuega.vuega_backend.Operator_pannel.Controller.scheduler.route_stops;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.vuega.vuega_backend.Operator_pannel.DTO.ResponseDto;
import net.vuega.vuega_backend.Operator_pannel.DTO.scheduler.route_stops.CreateRouteStopRequest;
import net.vuega.vuega_backend.Operator_pannel.DTO.scheduler.route_stops.RouteStopDTO;
import net.vuega.vuega_backend.Operator_pannel.DTO.scheduler.route_stops.UpdateRouteStopRequest;
import net.vuega.vuega_backend.Operator_pannel.Service.scheduler.route_stops.RouteStopService;

@RestController
@RequestMapping("/api/v1/route-stops")
@RequiredArgsConstructor
public class RouteStopController {

    private final RouteStopService routeStopService;

    /**
     * Create a new route stop.
     */
    @PostMapping
    public ResponseEntity<ResponseDto<RouteStopDTO>> createRouteStop(
            @Valid @RequestBody CreateRouteStopRequest request) {
        RouteStopDTO routeStop = routeStopService.createRouteStop(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.created(routeStop));
    }

    /**
     * Get a specific route stop by ID.
     */
    @GetMapping("/{stopId}")
    public ResponseEntity<ResponseDto<RouteStopDTO>> getRouteStop(@PathVariable Long stopId) {
        RouteStopDTO routeStop = routeStopService.getRouteStopById(stopId);
        return ResponseEntity.ok(ResponseDto.success(routeStop));
    }

    /**
     * Get all route stops for a route (ordered by stop order).
     */
    @GetMapping("/route/{routeId}")
    public ResponseEntity<ResponseDto<List<RouteStopDTO>>> getRouteStopsByRoute(@PathVariable Long routeId) {
        List<RouteStopDTO> stops = routeStopService.getRouteStopsByRoute(routeId);
        return ResponseEntity.ok(ResponseDto.success(stops));
    }

    /**
     * Get a specific route stop by route ID and stop order.
     */
    @GetMapping("/route/{routeId}/stop-order/{stopOrder}")
    public ResponseEntity<ResponseDto<RouteStopDTO>> getRouteStopByOrder(
            @PathVariable Long routeId,
            @PathVariable Integer stopOrder) {
        RouteStopDTO routeStop = routeStopService.getRouteStopByRouteAndOrder(routeId, stopOrder);
        return ResponseEntity.ok(ResponseDto.success(routeStop));
    }

    /**
     * Get all route stops for a city.
     */
    @GetMapping("/city/{cityId}")
    public ResponseEntity<ResponseDto<List<RouteStopDTO>>> getRouteStopsByCity(@PathVariable String cityId) {
        List<RouteStopDTO> stops = routeStopService.getRouteStopsByCity(cityId);
        return ResponseEntity.ok(ResponseDto.success(stops));
    }

    /**
     * Update a route stop.
     */
    @PutMapping("/{stopId}")
    public ResponseEntity<ResponseDto<RouteStopDTO>> updateRouteStop(
            @PathVariable Long stopId,
            @Valid @RequestBody UpdateRouteStopRequest request) {
        RouteStopDTO routeStop = routeStopService.updateRouteStop(stopId, request);
        return ResponseEntity.ok(ResponseDto.success(routeStop));
    }

    /**
     * Delete a specific route stop.
     */
    @DeleteMapping("/{stopId}")
    public ResponseEntity<ResponseDto<Void>> deleteRouteStop(@PathVariable Long stopId) {
        routeStopService.deleteRouteStop(stopId);
        return ResponseEntity.ok(ResponseDto.success(null));
    }

    /**
     * Delete all route stops for a route.
     */
    @DeleteMapping("/route/{routeId}")
    public ResponseEntity<ResponseDto<Void>> deleteRouteStopsByRoute(@PathVariable Long routeId) {
        routeStopService.deleteRouteStopsByRoute(routeId);
        return ResponseEntity.ok(ResponseDto.success(null));
    }
}
