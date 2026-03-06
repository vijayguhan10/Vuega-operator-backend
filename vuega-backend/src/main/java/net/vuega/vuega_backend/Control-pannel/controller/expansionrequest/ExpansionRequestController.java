package net.vuega.control_plane.controller.expansionrequest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.vuega.control_plane.dto.common.ApiResponse;
import net.vuega.control_plane.dto.expansionrequest.ExpansionRequestDto;
import net.vuega.control_plane.service.expansionrequest.ExpansionRequestService;

@RestController
@RequestMapping("/api/expansion-requests")
@RequiredArgsConstructor
public class ExpansionRequestController {

    private static final Logger logger = LoggerFactory.getLogger(ExpansionRequestController.class);
    private final ExpansionRequestService service;

    // ================= CREATE =================
    @PostMapping("/")
    public ResponseEntity<ApiResponse<ExpansionRequestDto>> createRequest(
            @Valid @RequestBody ExpansionRequestDto dto) {
        logger.info("POST request to create expansion request");

        ExpansionRequestDto response = service.createRequest(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        HttpStatus.CREATED.value(),
                        "Expansion request created successfully",
                        response));
    }

    // ================= GET ALL =================
    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<ExpansionRequestDto>>> getAllRequests() {
        logger.info("GET request to fetch all expansion requests");

        List<ExpansionRequestDto> requests = service.getAllRequests();
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Expansion requests fetched successfully",
                requests));
    }

    // ================= APPROVE =================
    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<ExpansionRequestDto>> approveRequest(
            @PathVariable Long id) {
        logger.info("PUT request to approve expansion request with ID: {}", id);

        ExpansionRequestDto approved = service.approveRequest(id);
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Expansion request approved successfully",
                approved));
    }

    // ================= REJECT =================
    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<ExpansionRequestDto>> rejectRequest(
            @PathVariable Long id) {
        logger.info("PUT request to reject expansion request with ID: {}", id);

        ExpansionRequestDto rejected = service.rejectRequest(id);
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Expansion request rejected successfully",
                rejected));
    }

    // ================= EXCEPTION HANDLERS =================
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("Illegal argument exception: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(
                        HttpStatus.BAD_REQUEST.value(),
                        ex.getMessage(),
                        null));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalStateException(IllegalStateException ex) {
        logger.warn("Illegal state exception: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(
                        HttpStatus.CONFLICT.value(),
                        ex.getMessage(),
                        null));
    }
}