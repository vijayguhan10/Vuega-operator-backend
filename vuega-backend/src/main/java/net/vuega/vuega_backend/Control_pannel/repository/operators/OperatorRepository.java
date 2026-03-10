package net.vuega.vuega_backend.Control_pannel.repository.operators;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.vuega.vuega_backend.Control_pannel.model.operators.Operators;
import net.vuega.vuega_backend.Control_pannel.util.OperatorStatus;

@Repository
public interface OperatorRepository extends JpaRepository<Operators, Long> {
    List<Operators> findByStatus(OperatorStatus status);

    boolean existsByOperatorNameAndCompanyName(String operatorName, String companyName);
    // List<Operator> findByIdList(Long operatorId);

}
