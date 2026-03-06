package net.vuega.control_plane.model.licenselimits;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonSetter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "license_limits")
public class LicenseLimits {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long limitId;

    @JsonAlias("licenseId")
    @Column(name = "license_id", nullable = false)
    private long licenseId;

    private long busLimit;

    private long routeLimit;

    private long patnerLimit;

    private long tripLimit;

    public Long getLimitId() {
        return limitId;
    }

    public void setLimitId(Long limitId) {
        this.limitId = limitId;
    }

    public long getLicenseId() {
        return licenseId;
    }

    @JsonSetter("license_id")
    public void setLicenseId(long licenseId) {
        this.licenseId = licenseId;
    }

    public long getBusLimit() {
        return busLimit;
    }

    public void setBusLimit(long busLimit) {
        this.busLimit = busLimit;
    }

    public long getRouteLimit() {
        return routeLimit;
    }

    public void setRouteLimit(long routeLimit) {
        this.routeLimit = routeLimit;
    }

    public long getPatnerLimit() {
        return patnerLimit;
    }

    public void setPatnerLimit(long patnerLimit) {
        this.patnerLimit = patnerLimit;
    }

    public long getTripLimit() {
        return tripLimit;
    }

    public void setTripLimit(long tripLimit) {
        this.tripLimit = tripLimit;
    }

    public LicenseLimits orElseThrow(Object object) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'orElseThrow'");
    }

}
