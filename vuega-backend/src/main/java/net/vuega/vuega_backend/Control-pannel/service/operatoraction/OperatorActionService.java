package net.vuega.control_plane.service.operatoraction;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.vuega.control_plane.dto.operatoraction.OperatorActionDto;
import net.vuega.control_plane.model.operatoraction.OperatorAction;
import net.vuega.control_plane.repository.operatoraction.OperatorActionRepository;
import net.vuega.control_plane.repository.operators.OperatorRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class OperatorActionService {

    private final OperatorActionRepository repository;
    private final OperatorRepository operatorRepository;

    // CREATE / UPDATE ACTION
    public OperatorActionDto applyAction(OperatorActionDto dto) {

        if (!operatorRepository.existsById(dto.getOperatorId()))
            throw new RuntimeException("Operator not found");

        if (dto.getAction() == null)
            throw new IllegalArgumentException("Action is required");

        OperatorAction action = new OperatorAction();
        action.setOperatorId(dto.getOperatorId());
        action.setAction(dto.getAction());
        action.setReason(dto.getReason());

        OperatorAction saved = repository.save(action);

        return new OperatorActionDto(
                saved.getActionId(),
                saved.getOperatorId(),
                saved.getAction(),
                saved.getReason()
        );
    }

    // GET CURRENT STATUS
    @Transactional(readOnly = true)
    public OperatorActionDto getCurrentAction(Long operatorId) {

        OperatorAction action = repository
                .findTopByOperatorIdOrderByCreatedAtDesc(operatorId)
                .orElseThrow(() -> 
                    new RuntimeException("No action found for operator"));

        return new OperatorActionDto(
                action.getActionId(),
                action.getOperatorId(),
                action.getAction(),
                action.getReason()
        );
    }
}