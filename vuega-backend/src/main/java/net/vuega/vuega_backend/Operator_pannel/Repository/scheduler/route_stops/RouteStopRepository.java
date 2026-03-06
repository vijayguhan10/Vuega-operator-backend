package net.vuega.vuega_backend.Repository.scheduler.route_stops;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.vuega.vuega_backend.Model.scheduler.route_stops.RouteStop;

@Repository
public interface RouteStopRepository extends JpaRepository<RouteStop, Long> {

    List<RouteStop> findByRouteId(Long routeId);

    Optional<RouteStop> findByRouteIdAndStopOrder(Long routeId, Integer stopOrder);

    List<RouteStop> findByRouteIdOrderByStopOrderAsc(Long routeId);

    List<RouteStop> findByCityId(String cityId);

    boolean existsByRouteIdAndStopOrder(Long routeId, Integer stopOrder);

    void deleteByRouteId(Long routeId);
}
