package net.vuega.control_plane.controller.routes;

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

import net.vuega.control_plane.dto.common.ApiResponse;
import net.vuega.control_plane.dto.routes.RouteDto;
import net.vuega.control_plane.service.routes.RouteService;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse<RouteDto>> createRoute(@RequestBody RouteDto dto) {
        RouteDto created = routeService.createRoute(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        HttpStatus.CREATED.value(),
                        "Route created successfully",
                        created));
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<RouteDto>>> getAllRoutes() {
        List<RouteDto> routes = routeService.getAllRoutes();
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Routes fetched successfully",
                routes));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RouteDto>> getRoute(@PathVariable Long id) {
        RouteDto route = routeService.getRouteById(id);
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Route fetched successfully",
                route));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RouteDto>> updateRoute(
            @PathVariable Long id,
            @RequestBody RouteDto dto) {
        RouteDto updated = routeService.updateRoute(id, dto);
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Route updated successfully",
                updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivateRoute(@PathVariable Long id) {
        routeService.deactivateRoute(id);
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Route deactivated successfully",
                null));
    }
}
