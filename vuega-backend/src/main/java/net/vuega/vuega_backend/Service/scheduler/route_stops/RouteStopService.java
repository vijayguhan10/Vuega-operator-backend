package net.vuega.vuega_backend.Service.scheduler.route_stops;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.vuega.vuega_backend.DTO.scheduler.route_stops.CreateRouteStopRequest;
import net.vuega.vuega_backend.DTO.scheduler.route_stops.RouteStopDTO;
import net.vuega.vuega_backend.DTO.scheduler.route_stops.UpdateRouteStopRequest;
import net.vuega.vuega_backend.exception.RouteStopNotFoundException;
import net.vuega.vuega_backend.Model.scheduler.route_stops.RouteStop;
import net.vuega.vuega_backend.Repository.scheduler.route_stops.RouteStopRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class RouteStopService {

    private final RouteStopRepository routeStopRepository;

    /**
     * Create a new route stop.
     */
    @Transactional
    public RouteStopDTO createRouteStop(CreateRouteStopRequest request) {
        log.info("Creating route stop for route {}, stop order {}", request.getRouteId(), request.getStopOrder());

        if (routeStopRepository.existsByRouteIdAndStopOrder(request.getRouteId(), request.getStopOrder())) {
            throw new IllegalArgumentException(
                    "Route stop with stop order " + request.getStopOrder() + " already exists for route "
                            + request.getRouteId());
        }

        RouteStop routeStop = RouteStop.builder()
                .routeId(request.getRouteId())
                .cityId(request.getCityId())
                .stopOrder(request.getStopOrder())
                .distanceFromSource(request.getDistanceFromSource())
                .departureOffset(request.getDepartureOffset())
                .build();

        RouteStop saved = routeStopRepository.save(routeStop);
        log.info("Route stop created with ID {}", saved.getStopId());
        return toDTO(saved);
    }

    /**
     * Get a specific route stop by ID.
     */
    @Transactional(readOnly = true)
    public RouteStopDTO getRouteStopById(Long stopId) {
        RouteStop routeStop = routeStopRepository.findById(stopId)
                .orElseThrow(() -> new RouteStopNotFoundException(stopId));
        return toDTO(routeStop);
    }

    /**
     * Get all route stops for a specific route, ordered by stop order.
     */
    @Transactional(readOnly = true)
    public List<RouteStopDTO> getRouteStopsByRoute(Long routeId) {
        return routeStopRepository.findByRouteIdOrderByStopOrderAsc(routeId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Get a specific route stop by route ID and stop order.
     */
    @Transactional(readOnly = true)
    public RouteStopDTO getRouteStopByRouteAndOrder(Long routeId, Integer stopOrder) {
        RouteStop routeStop = routeStopRepository.findByRouteIdAndStopOrder(routeId, stopOrder)
                .orElseThrow(() -> new RouteStopNotFoundException(
                        "Route stop with stop order " + stopOrder + " not found for route " + routeId));
        return toDTO(routeStop);
    }

    /**
     * Get all route stops for a specific city.
     */
    @Transactional(readOnly = true)
    public List<RouteStopDTO> getRouteStopsByCity(String cityId) {
        return routeStopRepository.findByCityId(cityId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Update a route stop.
     */
    @Transactional
    public RouteStopDTO updateRouteStop(Long stopId, UpdateRouteStopRequest request) {
        RouteStop routeStop = routeStopRepository.findById(stopId)
                .orElseThrow(() -> new RouteStopNotFoundException(stopId));

        if (request.getCityId() != null) {
            routeStop.setCityId(request.getCityId());
        }
        if (request.getStopOrder() != null) {
            routeStop.setStopOrder(request.getStopOrder());
        }
        if (request.getDistanceFromSource() != null) {
            routeStop.setDistanceFromSource(request.getDistanceFromSource());
        }
        if (request.getDepartureOffset() != null) {
            routeStop.setDepartureOffset(request.getDepartureOffset());
        }

        RouteStop updated = routeStopRepository.save(routeStop);
        log.info("Route stop {} updated", stopId);
        return toDTO(updated);
    }

    /**
     * Delete a route stop.
     */
    @Transactional
    public void deleteRouteStop(Long stopId) {
        if (!routeStopRepository.existsById(stopId)) {
            throw new RouteStopNotFoundException(stopId);
        }
        routeStopRepository.deleteById(stopId);
        log.info("Route stop {} deleted", stopId);
    }

    /**
     * Delete all route stops for a route.
     */
    @Transactional
    public void deleteRouteStopsByRoute(Long routeId) {
        routeStopRepository.deleteByRouteId(routeId);
        log.info("All route stops for route {} deleted", routeId);
    }

    private RouteStopDTO toDTO(RouteStop routeStop) {
        return RouteStopDTO.builder()
                .stopId(routeStop.getStopId())
                .routeId(routeStop.getRouteId())
                .cityId(routeStop.getCityId())
                .stopOrder(routeStop.getStopOrder())
                .distanceFromSource(routeStop.getDistanceFromSource())
                .departureOffset(routeStop.getDepartureOffset())
                .build();
    }
}
