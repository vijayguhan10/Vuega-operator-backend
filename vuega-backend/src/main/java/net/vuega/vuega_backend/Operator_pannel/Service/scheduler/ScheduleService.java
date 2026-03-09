package net.vuega.vuega_backend.Operator_pannel.Service.scheduler;

import java.util.List;

import org.springframework.stereotype.Service;

import net.vuega.vuega_backend.Operator_pannel.DTO.scheduler.CreateScheduleRequest;
import net.vuega.vuega_backend.Operator_pannel.DTO.scheduler.ScheduleDTO;
import net.vuega.vuega_backend.Operator_pannel.DTO.scheduler.UpdateScheduleRequest;
import net.vuega.vuega_backend.Operator_pannel.Model.scheduler.Schedule;
import net.vuega.vuega_backend.Operator_pannel.Model.scheduler.ScheduleStatus;
import net.vuega.vuega_backend.Operator_pannel.Repository.scheduler.ScheduleRepository;
import net.vuega.vuega_backend.Operator_pannel.Service.cache.ControlPanelCacheService;
import net.vuega.vuega_backend.Operator_pannel.exception.ScheduleOverlapException;

@Service
public class ScheduleService {

    private final ScheduleRepository repository;
    private final ControlPanelCacheService cacheService;

    public ScheduleService(
            ScheduleRepository repository,
            ControlPanelCacheService cacheService) {
        this.repository = repository;
        this.cacheService = cacheService;
    }

    // Creates a schedule after checking for time overlaps on the same bus.
    public ScheduleDTO createSchedule(CreateScheduleRequest request) {
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

    // Fetches a single schedule by ID, enriched with bus and route details.
    public ScheduleDTO getScheduleById(Long id) {
        Schedule schedule = repository.findById(id).orElse(null);
        if (schedule == null)
            return null;
        return enrichWithControlPlane(schedule);
    }

    // Returns all schedules enriched with Control Plane data.
    public List<ScheduleDTO> getAllSchedules() {
        return repository.findAll().stream()
                .map(this::enrichWithControlPlane)
                .toList();
    }

    // Partially updates a schedule; validates no overlap with existing ones.
    public ScheduleDTO updateSchedule(Long id, UpdateScheduleRequest request) {
        Schedule schedule = repository.findById(id).orElse(null);
        if (schedule == null)
            return null;

        Long busId = request.getBusId() != null ? request.getBusId() : schedule.getBusId();
        java.time.LocalTime departTime = request.getDepartTime() != null ? request.getDepartTime()
                : schedule.getDepartTime();
        java.time.LocalTime arriveTime = request.getArriveTime() != null ? request.getArriveTime()
                : schedule.getArriveTime();

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

    // Soft-deletes a schedule by setting its status to ABORTED.
    public ScheduleDTO deleteSchedule(Long id) {
        Schedule schedule = repository.findById(id).orElse(null);
        if (schedule == null)
            return null;

        schedule.setStatus(ScheduleStatus.ABORTED);
        Schedule saved = repository.save(schedule);
        return enrichWithControlPlane(saved);
    }

    // Filters schedules by bus ID.
    public List<ScheduleDTO> getSchedulesByBus(Long busId) {
        return repository.findByBusId(busId).stream()
                .map(this::enrichWithControlPlane)
                .toList();
    }

    // Filters schedules by route ID.
    public List<ScheduleDTO> getSchedulesByRoute(Long routeId) {
        return repository.findByRouteId(routeId).stream()
                .map(this::enrichWithControlPlane)
                .toList();
    }

    // Filters schedules by status (ACTIVE, INACTIVE, ABORTED).
    public List<ScheduleDTO> getSchedulesByStatus(ScheduleStatus status) {
        return repository.findByStatus(status).stream()
                .map(this::enrichWithControlPlane)
                .toList();
    }

    // Filters schedules by both bus and route.
    public List<ScheduleDTO> getSchedulesByBusAndRoute(Long busId, Long routeId) {
        return repository.findByBusIdAndRouteId(busId, routeId).stream()
                .map(this::enrichWithControlPlane)
                .toList();
    }

    // Toggles a schedule between ACTIVE and INACTIVE status.
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

    private ScheduleDTO enrichWithControlPlane(Schedule schedule) {
        ScheduleDTO dto = ScheduleDTO.builder()
                .scheduleId(schedule.getScheduleId())
                .busId(schedule.getBusId())
                .routeId(schedule.getRouteId())
                .departTime(schedule.getDepartTime())
                .arriveTime(schedule.getArriveTime())
                .status(schedule.getStatus())
                .build();

        dto.setBusDetails(cacheService.getBusDetails(schedule.getBusId()));
        dto.setRouteDetails(cacheService.getRouteDetails(schedule.getRouteId()));

        return dto;
    }
}
