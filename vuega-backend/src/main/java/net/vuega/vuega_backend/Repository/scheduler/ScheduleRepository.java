package net.vuega.vuega_backend.Repository.scheduler;

import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /**
     * Check if a bus already has a schedule that overlaps with the given time
     * range.
     * Overlap condition: existing.departTime < newArriveTime AND
     * existing.arriveTime > newDepartTime
     */
    @Query("SELECT COUNT(s) > 0 FROM Schedule s WHERE s.busId = :busId " +
            "AND s.status = 'ACTIVE' " +
            "AND s.departTime < :arriveTime " +
            "AND s.arriveTime > :departTime")
    boolean existsOverlappingSchedule(
            @Param("busId") Long busId,
            @Param("departTime") LocalTime departTime,
            @Param("arriveTime") LocalTime arriveTime);

    /**
     * Same as above but excludes a specific schedule (used for updates).
     */
    @Query("SELECT COUNT(s) > 0 FROM Schedule s WHERE s.busId = :busId " +
            "AND s.status = 'ACTIVE' " +
            "AND s.scheduleId <> :excludeId " +
            "AND s.departTime < :arriveTime " +
            "AND s.arriveTime > :departTime")
    boolean existsOverlappingScheduleExcluding(
            @Param("busId") Long busId,
            @Param("departTime") LocalTime departTime,
            @Param("arriveTime") LocalTime arriveTime,
            @Param("excludeId") Long excludeId);
}
