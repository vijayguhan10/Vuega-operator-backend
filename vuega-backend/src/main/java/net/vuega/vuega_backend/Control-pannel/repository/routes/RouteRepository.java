package net.vuega.control_plane.repository.routes;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.vuega.control_plane.model.routes.Route;
import net.vuega.control_plane.util.RouteStatus;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {

    List<Route> findByOperatorId(Long operatorId);

    List<Route> findByStatus(RouteStatus status);

    boolean existsByOperatorIdAndFromCityIdAndToCityId(
            Long operatorId,
            Long fromCityId,
            Long toCityId);
}
