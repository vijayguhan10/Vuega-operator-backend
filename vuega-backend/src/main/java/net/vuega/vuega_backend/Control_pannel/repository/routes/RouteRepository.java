package net.vuega.vuega_backend.Control_pannel.repository.routes;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.vuega.vuega_backend.Control_pannel.model.routes.Route;
import net.vuega.vuega_backend.Control_pannel.util.RouteStatus;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {

    List<Route> findByOperatorId(Long operatorId);

    List<Route> findByStatus(RouteStatus status);

    boolean existsByOperatorIdAndFromCityIdAndToCityId(
            Long operatorId,
            Long fromCityId,
            Long toCityId);
}
