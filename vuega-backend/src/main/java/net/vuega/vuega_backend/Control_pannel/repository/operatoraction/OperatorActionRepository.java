package net.vuega.vuega_backend.Control_pannel.repository.operatoraction;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import net.vuega.vuega_backend.Control_pannel.model.operatoraction.OperatorAction;

public interface OperatorActionRepository 
        extends JpaRepository<OperatorAction, Long> {

    Optional<OperatorAction> findTopByOperatorIdOrderByCreatedAtDesc(Long operatorId);
}