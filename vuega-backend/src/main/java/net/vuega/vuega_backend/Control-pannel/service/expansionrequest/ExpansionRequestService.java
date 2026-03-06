package net.vuega.control_plane.service.expansionrequest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.vuega.control_plane.dto.expansionrequest.ExpansionRequestDto;
import net.vuega.control_plane.model.expansionrequest.ExpansionRequest;
import net.vuega.control_plane.repository.expansionrequest.ExpansionRequestRepository;
import net.vuega.control_plane.repository.operators.OperatorRepository;
import net.vuega.control_plane.util.ExpansionRequestStatus;

@Service
@Transactional
public class ExpansionRequestService {

    private static final Logger logger = LoggerFactory.getLogger(ExpansionRequestService.class);

    private final ExpansionRequestRepository repository;
    private final OperatorRepository operatorRepository;

    public ExpansionRequestService(ExpansionRequestRepository repository, OperatorRepository operatorRepository) {
        this.repository = repository;
        this.operatorRepository = operatorRepository;
    }

    // ================= CREATE =================
    public ExpansionRequestDto createRequest(ExpansionRequestDto dto) {
        logger.info("Creating expansion request for operator: {}", dto.getOperatorId());

        if (dto.getOperatorId() == null)
            throw new IllegalArgumentException("Operator ID is required");

        if (!operatorRepository.existsById(dto.getOperatorId()))
            throw new IllegalArgumentException("Operator not found");

        if (dto.getRequestFor() == null)
            throw new IllegalArgumentException("Request type is required");

        if (dto.getCount() == null || dto.getCount() <= 0)
            throw new IllegalArgumentException("Count must be greater than 0");

        ExpansionRequest request = new ExpansionRequest();
        request.setOperatorId(dto.getOperatorId());
        request.setRequestFor(dto.getRequestFor());
        request.setCount(dto.getCount());
        request.setStatus(ExpansionRequestStatus.PENDING);

        ExpansionRequest saved = repository.save(request);
        logger.debug("Expansion request created with ID: {}", saved.getRequestId());

        return convertToDto(saved);
    }

    // ================= GET ALL =================
    @Transactional(readOnly = true)
    public List<ExpansionRequestDto> getAllRequests() {
        logger.info("Fetching all expansion requests");

        return repository.findAll()
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    // ================= APPROVE =================
    public ExpansionRequestDto approveRequest(Long id) {
        logger.info("Approving expansion request with ID: {}", id);

        ExpansionRequest request = repository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Expansion request not found with ID: {}", id);
                    return new IllegalArgumentException("Expansion request not found");
                });

        if (request.getStatus() != ExpansionRequestStatus.PENDING)
            throw new IllegalStateException("Request already processed");

        request.setStatus(ExpansionRequestStatus.APPROVED);

        ExpansionRequest updated = repository.save(request);
        logger.debug("Expansion request approved with ID: {}", id);

        return convertToDto(updated);
    }

    // ================= REJECT =================
    public ExpansionRequestDto rejectRequest(Long id) {
        logger.info("Rejecting expansion request with ID: {}", id);

        ExpansionRequest request = repository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Expansion request not found with ID: {}", id);
                    return new IllegalArgumentException("Expansion request not found");
                });

        if (request.getStatus() != ExpansionRequestStatus.PENDING)
            throw new IllegalStateException("Request already processed");

        request.setStatus(ExpansionRequestStatus.REJECTED);

        ExpansionRequest updated = repository.save(request);
        logger.debug("Expansion request rejected with ID: {}", id);

        return convertToDto(updated);
    }

    // ================= MAPPER =================
    private ExpansionRequestDto convertToDto(ExpansionRequest request) {

        return new ExpansionRequestDto(
                request.getRequestId(),
                request.getOperatorId(),
                request.getRequestFor(),
                request.getCount(),
                request.getStatus());
    }
}