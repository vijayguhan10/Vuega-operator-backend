package net.vuega.control_plane.model.buses;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import net.vuega.control_plane.util.BusStatus;
import net.vuega.control_plane.util.BusType;

@Entity
@Table(name = "buses")
public class Buses {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "bus_id")
    private Long busId;

    @Column(name = "operator_id", nullable = false)
    private Long operatorId;

    @Column(name = "bus_number", nullable = false)
    private String busNumber;

    @Column(name = "bus_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private BusType busType;

    @Column(name = "layout_id", nullable = false)
    private Long layoutId;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private BusStatus status;

    public Long getBusId() {
        return busId;
    }

    public void setBusId(Long busId) {
        this.busId = busId;
    }

    public long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public BusType getBusType() {
        return busType;
    }

    public void setBusType(BusType busType) {
        this.busType = busType;
    }

    public long getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(long layoutId) {
        this.layoutId = layoutId;
    }

    public BusStatus getStatus() {
        return status;
    }

    public void setStatus(BusStatus status) {
        this.status = status;
    }

}
