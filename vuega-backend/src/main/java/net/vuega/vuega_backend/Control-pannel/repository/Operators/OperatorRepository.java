package net.vuega.control_plane.repository.operators;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.vuega.control_plane.model.operators.Operators;
import net.vuega.control_plane.util.OperatorStatus;

@Repository
public interface OperatorRepository extends JpaRepository<Operators, Long> {
    List<Operators> findByStatus(OperatorStatus status);

    boolean existsByOperatorNameAndCompanyName(String operatorName, String companyName);
    // List<Operator> findByIdList(Long operatorId);

}
