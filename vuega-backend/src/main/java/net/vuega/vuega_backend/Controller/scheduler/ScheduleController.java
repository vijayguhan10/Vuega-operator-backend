package net.vuega.vuega_backend.Controller.scheduler;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.vuega.vuega_backend.DTO.ResponseDto;
import net.vuega.vuega_backend.DTO.scheduler.CreateScheduleRequest;
import net.vuega.vuega_backend.DTO.scheduler.ScheduleDTO;
import net.vuega.vuega_backend.DTO.scheduler.UpdateScheduleRequest;
import net.vuega.vuega_backend.Exception.ScheduleOverlapException;
import net.vuega.vuega_backend.Model.scheduler.ScheduleStatus;
import net.vuega.vuega_backend.Service.scheduler.ScheduleService;

// Schedule REST controller — CRUD and business queries.
@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService service;

    @PostMapping
    public ResponseEntity<ResponseDto<ScheduleDTO>> create(@RequestBody CreateScheduleRequest request) {
        try {
            ScheduleDTO created = service.createSchedule(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDto.created(created));
        } catch (ScheduleOverlapException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ResponseDto.error(409, e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ResponseDto<List<ScheduleDTO>>> getAll() {
        List<ScheduleDTO> list = service.getAllSchedules();
        return ResponseEntity.ok(ResponseDto.success(list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<ScheduleDTO>> getById(@PathVariable Long id) {
        ScheduleDTO dto = service.getScheduleById(id);
        if (dto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseDto.notFound("Schedule not found with id: " + id));
        }
        return ResponseEntity.ok(ResponseDto.success(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto<ScheduleDTO>> update(
            @PathVariable Long id, @RequestBody UpdateScheduleRequest request) {
        try {
            ScheduleDTO dto = service.updateSchedule(id, request);
            if (dto == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseDto.notFound("Schedule not found with id: " + id));
            }
            return ResponseEntity.ok(ResponseDto.success(dto));
        } catch (ScheduleOverlapException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ResponseDto.error(409, e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto<ScheduleDTO>> delete(@PathVariable Long id) {
        ScheduleDTO dto = service.deleteSchedule(id);
        if (dto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseDto.notFound("Schedule not found with id: " + id));
        }
        return ResponseEntity.ok(ResponseDto.success(dto));
    }

    @GetMapping("/bus/{busId}")
    public ResponseEntity<ResponseDto<List<ScheduleDTO>>> getByBus(@PathVariable Long busId) {
        return ResponseEntity.ok(ResponseDto.success(service.getSchedulesByBus(busId)));
    }

    @GetMapping("/route/{routeId}")
    public ResponseEntity<ResponseDto<List<ScheduleDTO>>> getByRoute(@PathVariable Long routeId) {
        return ResponseEntity.ok(ResponseDto.success(service.getSchedulesByRoute(routeId)));
    }

    @GetMapping("/status")
    public ResponseEntity<ResponseDto<List<ScheduleDTO>>> getByStatus(@RequestParam ScheduleStatus status) {
        return ResponseEntity.ok(ResponseDto.success(service.getSchedulesByStatus(status)));
    }

    @GetMapping("/bus/{busId}/route/{routeId}")
    public ResponseEntity<ResponseDto<List<ScheduleDTO>>> getByBusAndRoute(
            @PathVariable Long busId, @PathVariable Long routeId) {
        return ResponseEntity.ok(ResponseDto.success(service.getSchedulesByBusAndRoute(busId, routeId)));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ResponseDto<ScheduleDTO>> toggleStatus(@PathVariable Long id) {
        ScheduleDTO dto = service.toggleStatus(id);
        if (dto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseDto.notFound("Schedule not found with id: " + id));
        }
        return ResponseEntity.ok(ResponseDto.success(dto));
    }
}
