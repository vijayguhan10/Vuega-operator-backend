package net.vuega.vuega_backend.Control_pannel.repository.licenselimits;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.vuega.vuega_backend.Control_pannel.model.licenselimits.LicenseLimits;

@Repository
public interface LicenseLimitsRepository extends JpaRepository<LicenseLimits, Long> {
    LicenseLimits findByLicenseId(long licenseId);

}
