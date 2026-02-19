error id: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Service/scheduler/ScheduleService.java:_empty_/ScheduleDTO#setRouteDetails#
file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Service/scheduler/ScheduleService.java
empty definition using pc, found symbol in pc: _empty_/ScheduleDTO#setRouteDetails#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 7546
uri: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Service/scheduler/ScheduleService.java
text:
```scala
package net.vuega.vuega_backend.Service.scheduler;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import net.vuega.vuega_backend.DTO.scheduler.CreateScheduleRequest;
import net.vuega.vuega_backend.DTO.scheduler.ScheduleDTO;
import net.vuega.vuega_backend.DTO.scheduler.UpdateScheduleRequest;
import net.vuega.vuega_backend.Exception.ScheduleOverlapException;
import net.vuega.vuega_backend.Model.scheduler.Schedule;
import net.vuega.vuega_backend.Model.scheduler.ScheduleStatus;
import net.vuega.vuega_backend.Repository.scheduler.ScheduleRepository;

@Service
public class ScheduleService {

    private final ScheduleRepository repository;
    private final RestClient controlPlaneClient;

    public ScheduleService(
            ScheduleRepository repository,
            @Value("${control-plane.base-url:http://localhost:3000}") String controlPlaneBaseUrl) {
        this.repository = repository;
        this.controlPlaneClient = RestClient.builder()
                .baseUrl(controlPlaneBaseUrl)
                .build();
    }

    // ======================== CRUD ========================

    /**
     * Create a new schedule.
     */
    public ScheduleDTO createSchedule(CreateScheduleRequest request) {
        // Check for overlapping schedule on the same bus
        if (repository.existsOverlappingSchedule(
                request.getBusId(),
                request.getDepartTime(),
                request.getArriveTime())) {
            throw new ScheduleOverlapException(
                    "Bus " + request.getBusId() + " already has a schedule between " +
                            request.getDepartTime() + " and " + request.getArriveTime());
        }

        Schedule schedule = Schedule.builder()
                .busId(request.getBusId())
                .routeId(request.getRouteId())
                .departTime(request.getDepartTime())
                .arriveTime(request.getArriveTime())
                .status(request.getStatus() != null ? request.getStatus() : ScheduleStatus.ACTIVE)
                .build();

        Schedule saved = repository.save(schedule);
        return enrichWithControlPlane(saved);
    }

    /**
     * Get a single schedule by ID (enriched with bus & route details).
     */
    public ScheduleDTO getScheduleById(Long id) {
        Schedule schedule = repository.findById(id).orElse(null);
        if (schedule == null)
            return null;
        return enrichWithControlPlane(schedule);
    }

    /**
     * Get all schedules (enriched).
     */
    public List<ScheduleDTO> getAllSchedules() {
        return repository.findAll().stream()
                .map(this::enrichWithControlPlane)
                .toList();
    }

    /**
     * Update an existing schedule.
     */
    public ScheduleDTO updateSchedule(Long id, UpdateScheduleRequest request) {
        Schedule schedule = repository.findById(id).orElse(null);
        if (schedule == null)
            return null;

        // Determine final values after update
        Long busId = request.getBusId() != null ? request.getBusId() : schedule.getBusId();
        java.time.LocalTime departTime = request.getDepartTime() != null ? request.getDepartTime()
                : schedule.getDepartTime();
        java.time.LocalTime arriveTime = request.getArriveTime() != null ? request.getArriveTime()
                : schedule.getArriveTime();

        // Check for overlapping schedule on the same bus (excluding current schedule)
        if (repository.existsOverlappingScheduleExcluding(busId, departTime, arriveTime, id)) {
            throw new ScheduleOverlapException(
                    "Bus " + busId + " already has a schedule between " +
                            departTime + " and " + arriveTime);
        }

        if (request.getBusId() != null)
            schedule.setBusId(request.getBusId());
        if (request.getRouteId() != null)
            schedule.setRouteId(request.getRouteId());
        if (request.getDepartTime() != null)
            schedule.setDepartTime(request.getDepartTime());
        if (request.getArriveTime() != null)
            schedule.setArriveTime(request.getArriveTime());
        if (request.getStatus() != null)
            schedule.setStatus(request.getStatus());

        Schedule saved = repository.save(schedule);
        return enrichWithControlPlane(saved);
    }

    /**
     * Soft-delete a schedule by setting status to ABORTED.
     */
    public ScheduleDTO deleteSchedule(Long id) {
        Schedule schedule = repository.findById(id).orElse(null);
        if (schedule == null)
            return null;

        schedule.setStatus(ScheduleStatus.ABORTED);
        Schedule saved = repository.save(schedule);
        return enrichWithControlPlane(saved);
    }

    // ======================== BUSINESS QUERIES ========================

    /**
     * Get all schedules for a specific bus.
     */
    public List<ScheduleDTO> getSchedulesByBus(Long busId) {
        return repository.findByBusId(busId).stream()
                .map(this::enrichWithControlPlane)
                .toList();
    }

    /**
     * Get all schedules for a specific route.
     */
    public List<ScheduleDTO> getSchedulesByRoute(Long routeId) {
        return repository.findByRouteId(routeId).stream()
                .map(this::enrichWithControlPlane)
                .toList();
    }

    /**
     * Get all schedules by status (ACTIVE / INACTIVE).
     */
    public List<ScheduleDTO> getSchedulesByStatus(ScheduleStatus status) {
        return repository.findByStatus(status).stream()
                .map(this::enrichWithControlPlane)
                .toList();
    }

    /**
     * Get schedules for a bus + route combination.
     */
    public List<ScheduleDTO> getSchedulesByBusAndRoute(Long busId, Long routeId) {
        return repository.findByBusIdAndRouteId(busId, routeId).stream()
                .map(this::enrichWithControlPlane)
                .toList();
    }


    /**
     * Toggle schedule status (ACTIVE ↔ INACTIVE).
     */
    public ScheduleDTO toggleStatus(Long id) {
        Schedule schedule = repository.findById(id).orElse(null);
        if (schedule == null)
            return null;

        schedule.setStatus(
                schedule.getStatus() == ScheduleStatus.ACTIVE
                        ? ScheduleStatus.INACTIVE
                        : ScheduleStatus.ACTIVE);

        Schedule saved = repository.save(schedule);
        return enrichWithControlPlane(saved);
    }

    // ======================== CONTROL PLANE INTEGRATION ========================

    /**
     * Converts entity to DTO and enriches with bus & route details from Control
     * Plane.
     */
    private ScheduleDTO enrichWithControlPlane(Schedule schedule) {
        ScheduleDTO dto = ScheduleDTO.builder()
                .scheduleId(schedule.getScheduleId())
                .busId(schedule.getBusId())
                .routeId(schedule.getRouteId())
                .departTime(schedule.getDepartTime())
                .arriveTime(schedule.getArriveTime())
                .status(schedule.getStatus())
                .build();

        // Fetch bus details from Control Plane
        dto.setBusDetails(fetchBusDetails(schedule.getBusId()));

        // Fetch route details from Control Plane
        dto.setRouteD@@etails(fetchRouteDetails(schedule.getRouteId()));

        return dto;
    }

    /**
     * Fetch bus info from Control Plane: GET
     * http://localhost:3000/api/controlplane/buses/{busId}
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> fetchBusDetails(Long busId) {
        try {
            return controlPlaneClient.get()
                    .uri("/api/controlplane/buses/{busId}", busId)
                    .retrieve()
                    .body(Map.class);
        } catch (HttpClientErrorException e) {
            return Map.of("error", "Bus not found", "busId", busId);
        } catch (Exception e) {
            return Map.of("error", "Control Plane unavailable", "busId", busId);
        }
    }

    /**
     * Fetch route info from Control Plane: GET
     * http://localhost:3000/api/controlplane/routes/{routeId}
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> fetchRouteDetails(Long routeId) {
        try {
            return controlPlaneClient.get()
                    .uri("/api/controlplane/routes/{routeId}", routeId)
                    .retrieve()
                    .body(Map.class);
        } catch (HttpClientErrorException e) {
            return Map.of("error", "Route not found", "routeId", routeId);
        } catch (Exception e) {
            return Map.of("error", "Control Plane unavailable", "routeId", routeId);
        }
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/ScheduleDTO#setRouteDetails#