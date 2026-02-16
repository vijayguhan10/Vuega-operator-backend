package net.vuega.vuega_backend.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.vuega.vuega_backend.Model.operator_config.OperatorConfig;

@Repository
public interface OperatorConfigRepository extends JpaRepository<OperatorConfig, Long> {

    Optional<OperatorConfig> findByLicenseKey(String licenseKey);

    boolean existsByLicenseKey(String licenseKey);
}
