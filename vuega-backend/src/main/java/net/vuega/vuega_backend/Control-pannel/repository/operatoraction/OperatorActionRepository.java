package net.vuega.control_plane.repository.operatoraction;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import net.vuega.control_plane.model.operatoraction.OperatorAction;

public interface OperatorActionRepository 
        extends JpaRepository<OperatorAction, Long> {

    Optional<OperatorAction> findTopByOperatorIdOrderByCreatedAtDesc(Long operatorId);
}