package net.vuega.control_plane.service.routes;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.vuega.control_plane.dto.routes.RouteDto;
import net.vuega.control_plane.model.routes.Route;
import net.vuega.control_plane.repository.city.CityRepository;
import net.vuega.control_plane.repository.operators.OperatorRepository;
import net.vuega.control_plane.repository.routes.RouteRepository;
import net.vuega.control_plane.util.RouteStatus;

@Service
@Transactional
public class RouteService {

    private final RouteRepository routeRepository;
    private final OperatorRepository operatorRepository;
    private final CityRepository cityRepository;

    public RouteService(RouteRepository routeRepository,
            OperatorRepository operatorRepository,
            CityRepository cityRepository) {
        this.routeRepository = routeRepository;
        this.operatorRepository = operatorRepository;
        this.cityRepository = cityRepository;
    }

    // ================= CREATE =================
    public RouteDto createRoute(RouteDto dto) {

        validate(dto);

        if (!operatorRepository.existsById(dto.getOperatorId())) {
            throw new RuntimeException("Operator not found");
        }

        if (!cityRepository.existsById(dto.getFromCityId())) {
            throw new RuntimeException("From city not found");
        }

        if (!cityRepository.existsById(dto.getToCityId())) {
            throw new RuntimeException("To city not found");
        }

        if (dto.getFromCityId().equals(dto.getToCityId())) {
            throw new IllegalArgumentException("From and To city cannot be same");
        }

        if (routeRepository.existsByOperatorIdAndFromCityIdAndToCityId(
                dto.getOperatorId(),
                dto.getFromCityId(),
                dto.getToCityId())) {
            throw new IllegalArgumentException("Route already exists for this operator");
        }

        Route route = new Route();
        route.setOperatorId(dto.getOperatorId());
        route.setFromCityId(dto.getFromCityId());
        route.setToCityId(dto.getToCityId());
        route.setTotalDistance(dto.getTotalDistance());
        route.setStatus(dto.getStatus());

        Route saved = routeRepository.save(route);

        return convertToDto(saved);
    }

    // ================= GET ALL =================
    @Transactional(readOnly = true)
    public List<RouteDto> getAllRoutes() {
        return routeRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // ================= GET BY ID =================
    @Transactional(readOnly = true)
    public RouteDto getRouteById(Long id) {

        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Route not found with id: " + id));

        return convertToDto(route);
    }

    // ================= UPDATE =================
    public RouteDto updateRoute(Long id, RouteDto dto) {

        validateForUpdate(dto);

        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Route not found"));

        if (dto.getFromCityId() != null && !cityRepository.existsById(dto.getFromCityId())) {
            throw new RuntimeException("From city not found");
        }

        if (dto.getToCityId() != null && !cityRepository.existsById(dto.getToCityId())) {
            throw new RuntimeException("To city not found");
        }

        if (dto.getFromCityId() != null && dto.getToCityId() != null &&
                dto.getFromCityId().equals(dto.getToCityId())) {
            throw new IllegalArgumentException("From and To city cannot be same");
        }

        // Update only provided fields
        if (dto.getFromCityId() != null)
            route.setFromCityId(dto.getFromCityId());
        if (dto.getToCityId() != null)
            route.setToCityId(dto.getToCityId());
        if (dto.getTotalDistance() != null)
            route.setTotalDistance(dto.getTotalDistance());
        if (dto.getStatus() != null)
            route.setStatus(dto.getStatus());

        Route updated = routeRepository.save(route);

        return convertToDto(updated);
    }

    // ================= SOFT DELETE =================
    public void deactivateRoute(Long id) {

        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Route not found"));

        if (route.getStatus() == RouteStatus.INACTIVE) {
            throw new IllegalStateException("Route already inactive");
        }

        route.setStatus(RouteStatus.INACTIVE);
        routeRepository.save(route);
    }

    // ================= VALIDATION =================
    private void validate(RouteDto dto) {

        if (dto == null)
            throw new IllegalArgumentException("Route cannot be null");

        if (dto.getOperatorId() == null)
            throw new IllegalArgumentException("Operator ID required");

        if (dto.getFromCityId() == null)
            throw new IllegalArgumentException("From city ID required");

        if (dto.getToCityId() == null)
            throw new IllegalArgumentException("To city ID required");

        if (dto.getTotalDistance() == null || dto.getTotalDistance() <= 0)
            throw new IllegalArgumentException("Total distance must be greater than 0");

        if (dto.getStatus() == null)
            throw new IllegalArgumentException("Status required");
    }

    private void validateForUpdate(RouteDto dto) {

        if (dto == null)
            throw new IllegalArgumentException("Route cannot be null");

        if (dto.getTotalDistance() != null && dto.getTotalDistance() <= 0)
            throw new IllegalArgumentException("Total distance must be greater than 0");
    }

    private RouteDto convertToDto(Route route) {
        return new RouteDto(
                route.getRouteId(),
                route.getOperatorId(),
                route.getFromCityId(),
                route.getToCityId(),
                route.getTotalDistance(),
                route.getStatus());
    }
}
