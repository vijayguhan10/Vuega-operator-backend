package net.vuega.control_plane.controller.heartbeats;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.vuega.control_plane.dto.common.ApiResponse;
import net.vuega.control_plane.dto.heartbeats.HeartBeatDto;
import net.vuega.control_plane.service.heartbeats.HeartBeatService;

@RestController
@RequestMapping("/api/heartbeats")
@RequiredArgsConstructor
public class HeartBeatController {

    private final HeartBeatService service;

    // ================= POST =================
    @PostMapping("/")
    public ResponseEntity<ApiResponse<HeartBeatDto>> receiveHeartbeat(
            @RequestBody HeartBeatDto dto) {

        HeartBeatDto created = service.receiveHeartbeat(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        HttpStatus.CREATED.value(),
                        "Heartbeat received successfully",
                        created));
    }

    // ================= GET =================
    @GetMapping("/{operatorId}")
    public ResponseEntity<ApiResponse<List<HeartBeatDto>>> getByOperator(
            @PathVariable Long operatorId) {

        List<HeartBeatDto> heartbeats = service.getByOperator(operatorId);
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Heartbeats fetched successfully",
                heartbeats));
    }
}