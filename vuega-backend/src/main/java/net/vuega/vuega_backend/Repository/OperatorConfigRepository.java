package net.vuega.vuega_backend.Repository;

import net.vuega.vuega_backend.Model.operator_config.OperatorConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OperatorConfigRepository extends JpaRepository<OperatorConfig, Long> {

    Optional<OperatorConfig> findByLicenseKey(String licenseKey);

    boolean existsByLicenseKey(String licenseKey);
}
