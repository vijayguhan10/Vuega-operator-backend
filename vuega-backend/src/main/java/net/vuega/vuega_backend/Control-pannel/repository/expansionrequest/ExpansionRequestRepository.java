package net.vuega.control_plane.repository.expansionrequest;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.vuega.control_plane.model.expansionrequest.ExpansionRequest;
import net.vuega.control_plane.util.ExpansionRequestStatus;

@Repository
public interface ExpansionRequestRepository 
        extends JpaRepository<ExpansionRequest, Long> {

    List<ExpansionRequest> findByOperatorId(Long operatorId);

    List<ExpansionRequest> findByStatus(ExpansionRequestStatus status);
}