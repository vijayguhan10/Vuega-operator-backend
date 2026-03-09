package net.vuega.vuega_backend.Control_pannel.controller.operatoraction;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.vuega.vuega_backend.Control_pannel.dto.common.ApiResponse;
import net.vuega.vuega_backend.Control_pannel.dto.operatoraction.OperatorActionDto;
import net.vuega.vuega_backend.Control_pannel.service.operatoraction.OperatorActionService;

@RestController
@RequestMapping("/api/actions")
@RequiredArgsConstructor
public class OperatorActionController {

    private final OperatorActionService service;

    @PostMapping("/")
    public ResponseEntity<ApiResponse<OperatorActionDto>> applyAction(
            @Valid @RequestBody OperatorActionDto dto) {

        OperatorActionDto created = service.applyAction(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        HttpStatus.CREATED.value(),
                        "Action applied successfully",
                        created));
    }

    @GetMapping("/{operatorId}")
    public ResponseEntity<ApiResponse<OperatorActionDto>> getCurrentStatus(
            @PathVariable Long operatorId) {

        OperatorActionDto action = service.getCurrentAction(operatorId);
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Current action fetched successfully",
                action));
    }
}