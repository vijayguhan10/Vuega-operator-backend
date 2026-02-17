package net.vuega.vuega_backend.Repository.scheduler;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.vuega.vuega_backend.Model.scheduler.Schedule;
import net.vuega.vuega_backend.Model.scheduler.ScheduleStatus;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // Find all schedules for a specific bus
    List<Schedule> findByBusId(Long busId);

    // Find all schedules for a specific route
    List<Schedule> findByRouteId(Long routeId);

    // Find all schedules by status (ACTIVE / INACTIVE)
    List<Schedule> findByStatus(ScheduleStatus status);

    // Find schedules for a specific bus and route combination
    List<Schedule> findByBusIdAndRouteId(Long busId, Long routeId);

    // Find active schedules for a specific bus
    List<Schedule> findByBusIdAndStatus(Long busId, ScheduleStatus status);

    // Find active schedules for a specific route
    List<Schedule> findByRouteIdAndStatus(Long routeId, ScheduleStatus status);
}
