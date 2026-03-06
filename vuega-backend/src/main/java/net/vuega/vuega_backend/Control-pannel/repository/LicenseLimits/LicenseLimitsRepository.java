package net.vuega.control_plane.repository.licenselimits;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.vuega.control_plane.model.licenselimits.LicenseLimits;

@Repository
public interface LicenseLimitsRepository extends JpaRepository<LicenseLimits, Long> {
    LicenseLimits findByLicenseId(long licenseId);

}
