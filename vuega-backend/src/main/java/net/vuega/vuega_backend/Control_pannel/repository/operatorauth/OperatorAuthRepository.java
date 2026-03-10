package net.vuega.vuega_backend.Control_pannel.repository.operatorauth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import net.vuega.vuega_backend.Control_pannel.model.operatorauth.OperatorAuth;

public interface OperatorAuthRepository extends JpaRepository<OperatorAuth, Long> {

    Optional<OperatorAuth> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByLicenceId(String licenceId);
}