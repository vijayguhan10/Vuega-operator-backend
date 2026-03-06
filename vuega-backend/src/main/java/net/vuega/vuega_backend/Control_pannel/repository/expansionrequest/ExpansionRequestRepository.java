package net.vuega.vuega_backend.Control_pannel.repository.expansionrequest;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.vuega.vuega_backend.Control_pannel.model.expansionrequest.ExpansionRequest;
import net.vuega.vuega_backend.Control_pannel.util.ExpansionRequestStatus;

@Repository
public interface ExpansionRequestRepository 
        extends JpaRepository<ExpansionRequest, Long> {

    List<ExpansionRequest> findByOperatorId(Long operatorId);

    List<ExpansionRequest> findByStatus(ExpansionRequestStatus status);
}