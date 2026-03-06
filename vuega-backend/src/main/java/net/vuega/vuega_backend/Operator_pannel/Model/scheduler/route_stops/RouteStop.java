package net.vuega.vuega_backend.Operator_pannel.Model.scheduler.route_stops;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "route_stops")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteStop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stop_id")
    private Long stopId;

    @Column(name = "route_id", nullable = false)
    private Long routeId;

    @Column(name = "city_id", nullable = false)
    private String cityId;

    @Column(name = "stop_order", nullable = false)
    private Integer stopOrder;

    @Column(name = "distance_from_source", nullable = false)
    private Integer distanceFromSource;

    @Column(name = "departure_offset", nullable = false)
    private Integer departureOffset;
}
