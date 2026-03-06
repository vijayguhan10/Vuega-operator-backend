package net.vuega.control_plane.controller.operatoraction;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import net.vuega.control_plane.dto.operatoraction.OperatorActionDto;
import net.vuega.control_plane.service.operatoraction.OperatorActionService;

@RestController
@RequestMapping("/api/actions")
@RequiredArgsConstructor
public class OperatorActionController {

    private final OperatorActionService service;

    @PostMapping("/")
    public ResponseEntity<OperatorActionDto> applyAction(
            @RequestBody OperatorActionDto dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.applyAction(dto));
    }

    @GetMapping("/{operatorId}")
    public ResponseEntity<OperatorActionDto> getCurrentStatus(
            @PathVariable Long operatorId) {

        return ResponseEntity.ok(service.getCurrentAction(operatorId));
    }
}