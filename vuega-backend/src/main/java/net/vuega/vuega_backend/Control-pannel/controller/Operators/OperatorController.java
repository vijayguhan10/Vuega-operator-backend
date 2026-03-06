package net.vuega.control_plane.controller.operators;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.vuega.control_plane.dto.common.ApiResponse;
import net.vuega.control_plane.dto.operators.OperatorsDto;
import net.vuega.control_plane.service.operators.OperatorService;

@RestController
@RequestMapping("/api/operators")
public class OperatorController {

    private final OperatorService operatorService;

    public OperatorController(OperatorService operatorService) {
        this.operatorService = operatorService;
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<OperatorsDto>>> getAllOperators() {
        List<OperatorsDto> operators = operatorService.getAllOperators();
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Operators fetched successfully",
                operators));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OperatorsDto>> getOperatorById(@PathVariable Long id) {

        OperatorsDto dto = operatorService.getOperatorById(id)
                .orElseThrow(() -> new RuntimeException("Operator not found"));

        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Operator fetched successfully",
                dto));
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse<OperatorsDto>> createOperator(@RequestBody OperatorsDto dto) {

        OperatorsDto created = operatorService.createOperator(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        HttpStatus.CREATED.value(),
                        "Operator created successfully",
                        created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OperatorsDto>> updateOperator(
            @PathVariable Long id,
            @RequestBody OperatorsDto dto) {

        OperatorsDto updated = operatorService.updateOperator(id, dto);

        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Operator updated successfully",
                updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOperator(@PathVariable Long id) {

        operatorService.deleteOperator(id);

        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Operator deleted successfully",
                null));
    }
}
