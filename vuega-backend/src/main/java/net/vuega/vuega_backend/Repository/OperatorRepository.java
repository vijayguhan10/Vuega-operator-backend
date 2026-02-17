package net.vuega.vuega_backend.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.vuega.vuega_backend.Model.Operator;

@Repository
public interface OperatorRepository extends JpaRepository<Operator, Long> {

    Optional<Operator> findByServiceEmail(String serviceEmail);

    boolean existsByServiceEmail(String serviceEmail);
}
