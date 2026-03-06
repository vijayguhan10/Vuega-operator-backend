package net.vuega.control_plane.service.operators;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.vuega.control_plane.dto.operators.OperatorsDto;
import net.vuega.control_plane.model.operators.Operators;
import net.vuega.control_plane.repository.operators.OperatorRepository;
import net.vuega.control_plane.util.OperatorStatus;

@Service
public class OperatorService {

    private final OperatorRepository operatorRepository;

    public OperatorService(OperatorRepository operatorRepository) {
        this.operatorRepository = operatorRepository;
    }

    // ================= GET ALL =================
    @Transactional
    public List<OperatorsDto> getAllOperators() {
        return operatorRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // ================= GET BY ID =================
    @Transactional
    public Optional<OperatorsDto> getOperatorById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Invalid operator ID");
        }

        return operatorRepository.findById(id)
                .map(this::convertToDto);
    }

    // ================= CREATE =================
    @Transactional
    public OperatorsDto createOperator(OperatorsDto dto) {
        validate(dto);

        if (operatorRepository.existsByOperatorNameAndCompanyName(
                dto.getOperatorName(),
                dto.getCompanyName())) {
            throw new IllegalArgumentException("Operator already exists");
        }

        Operators entity = convertToEntity(dto);
        Operators saved = operatorRepository.save(entity);

        return convertToDto(saved);
    }

    // ================= UPDATE =================
    @Transactional
    public OperatorsDto updateOperator(Long id, OperatorsDto dto) {
        if (id == null) {
            throw new IllegalArgumentException("Invalid operator ID");
        }

        validate(dto);

        Operators existing = operatorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Operator not found"));

        existing.setOperatorName(dto.getOperatorName());
        existing.setCompanyName(dto.getCompanyName());
        existing.setStatus(dto.getStatus());

        Operators updated = operatorRepository.save(existing);

        return convertToDto(updated);
    }

    // ================= SOFT DELETE =================
    @Transactional
    public void deleteOperator(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Invalid operator ID");
        }

        Operators existing = operatorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Operator not found"));

        if (existing.getStatus() == OperatorStatus.INACTIVE) {
            throw new IllegalStateException("Operator already inactive");
        }

        existing.setStatus(OperatorStatus.INACTIVE);
        operatorRepository.save(existing);
    }

    // ================= MAPPING =================
    private OperatorsDto convertToDto(Operators entity) {
        return new OperatorsDto(
                entity.getOperatorId(),
                entity.getOperatorName(),
                entity.getCompanyName(),
                entity.getStatus());
    }

    private Operators convertToEntity(OperatorsDto dto) {
        return Operators.builder()
                .operatorName(dto.getOperatorName())
                .companyName(dto.getCompanyName())
                .status(dto.getStatus())
                .build();
    }

    private void validate(OperatorsDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Operator cannot be null");
        }
        if (dto.getOperatorName() == null || dto.getOperatorName().isBlank()) {
            throw new IllegalArgumentException("Operator name cannot be empty");
        }
        if (dto.getCompanyName() == null || dto.getCompanyName().isBlank()) {
            throw new IllegalArgumentException("Company name cannot be empty");
        }
        if (dto.getStatus() == null) {
            throw new IllegalArgumentException("Operator status cannot be null");
        }
    }
}
