error id: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Controller/scheduler/ScheduleController.java:_empty_/HttpStatus#
file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Controller/scheduler/ScheduleController.java
empty definition using pc, found symbol in pc: _empty_/HttpStatus#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 1794
uri: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Controller/scheduler/ScheduleController.java
text:
```scala
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

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService service;

    // ======================== CRUD ========================

    /**
     * POST /api/schedules — Create a new schedule
     */
    @PostMapping
    public ResponseEntity<ResponseDto<ScheduleDTO>> create(@RequestBody CreateScheduleRequest request) {
        try {
            ScheduleDTO created = service.createSchedule(request);
            return ResponseEntity.status(HttpSta@@tus.CREATED).body(ResponseDto.created(created));
        } catch (ScheduleOverlapException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ResponseDto.error(409, e.getMessage()));
        }
    }

    /**
     * GET /api/schedules — Get all schedules
     */
    @GetMapping
    public ResponseEntity<ResponseDto<List<ScheduleDTO>>> getAll() {
        List<ScheduleDTO> list = service.getAllSchedules();
        return ResponseEntity.ok(ResponseDto.success(list));
    }

    /**
     * GET /api/schedules/{id} — Get schedule by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<ScheduleDTO>> getById(@PathVariable Long id) {
        ScheduleDTO dto = service.getScheduleById(id);
        if (dto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseDto.notFound("Schedule not found with id: " + id));
        }
        return ResponseEntity.ok(ResponseDto.success(dto));
    }

    /**
     * PUT /api/schedules/{id} — Update schedule
     */
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

    /**
     * DELETE /api/schedules/{id} — Soft-delete (set status to ABORTED)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto<ScheduleDTO>> delete(@PathVariable Long id) {
        ScheduleDTO dto = service.deleteSchedule(id);
        if (dto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseDto.notFound("Schedule not found with id: " + id));
        }
        return ResponseEntity.ok(ResponseDto.success(dto));
    }

    // ======================== BUSINESS ENDPOINTS ========================

    /**
     * GET /api/schedules/bus/{busId} — Get all schedules for a bus
     */
    @GetMapping("/bus/{busId}")
    public ResponseEntity<ResponseDto<List<ScheduleDTO>>> getByBus(@PathVariable Long busId) {
        return ResponseEntity.ok(ResponseDto.success(service.getSchedulesByBus(busId)));
    }

    /**
     * GET /api/schedules/route/{routeId} — Get all schedules for a route
     */
    @GetMapping("/route/{routeId}")
    public ResponseEntity<ResponseDto<List<ScheduleDTO>>> getByRoute(@PathVariable Long routeId) {
        return ResponseEntity.ok(ResponseDto.success(service.getSchedulesByRoute(routeId)));
    }

    /**
     * GET /api/schedules/status?status=ACTIVE — Get schedules by status
     */
    @GetMapping("/status")
    public ResponseEntity<ResponseDto<List<ScheduleDTO>>> getByStatus(@RequestParam ScheduleStatus status) {
        return ResponseEntity.ok(ResponseDto.success(service.getSchedulesByStatus(status)));
    }

    /**
     * GET /api/schedules/bus/{busId}/route/{routeId} — Get schedules for a bus +
     * route combo
     */
    @GetMapping("/bus/{busId}/route/{routeId}")
    public ResponseEntity<ResponseDto<List<ScheduleDTO>>> getByBusAndRoute(
            @PathVariable Long busId, @PathVariable Long routeId) {
        return ResponseEntity.ok(ResponseDto.success(service.getSchedulesByBusAndRoute(busId, routeId)));
    }

  
    /**
     * PATCH /api/schedules/{id}/toggle — Toggle ACTIVE ↔ INACTIVE
     */
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

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/HttpStatus#