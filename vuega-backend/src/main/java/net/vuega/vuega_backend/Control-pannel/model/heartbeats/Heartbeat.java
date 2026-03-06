package net.vuega.control_plane.model.heartbeats;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "heartbeats")
public class Heartbeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "heartbeat_id")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long heartbeatId;

    @Column(name = "operator_id", nullable = false)
    private Long operatorId;

    @Column(name = "bus_count", nullable = false)
    private Integer busCount;

    @Column(name = "route_count", nullable = false)
    private Integer routeCount;

    @Column(name = "trip_count", nullable = false)
    private Integer tripCount;

    public Long getHeartbeatId() {
        return heartbeatId;
    }

    public void setHeartbeatId(Long heartbeatId) {
        this.heartbeatId = heartbeatId;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public Integer getBusCount() {
        return busCount;
    }

    public void setBusCount(Integer busCount) {
        this.busCount = busCount;
    }

    public Integer getRouteCount() {
        return routeCount;
    }

    public void setRouteCount(Integer routeCount) {
        this.routeCount = routeCount;
    }

    public Integer getTripCount() {
        return tripCount;
    }

    public void setTripCount(Integer tripCount) {
        this.tripCount = tripCount;
    }

}