package net.vuega.control_plane.repository.licenses;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import net.vuega.control_plane.model.licenses.Licenses;
import net.vuega.control_plane.util.LicenseStatus;

@Repository
public interface LicenseRepository extends JpaRepository<Licenses, Long> {
    List<Licenses> findByStatus(LicenseStatus status);

    boolean existsByLicenseId(long licenseId, String licenseKey);

    boolean existsByLicenseKey(String licenseKey);

    @Modifying
    @Query("delete from Licenses l where l.licenseId = :licenseId")
    int deleteByLicenseId(@Param("licenseId") long licenseId);
}
