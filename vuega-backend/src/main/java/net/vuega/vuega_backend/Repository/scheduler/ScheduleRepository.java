package net.vuega.vuega_backend.Repository.scheduler;

import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import net.vuega.vuega_backend.Model.scheduler.Schedule;
import net.vuega.vuega_backend.Model.scheduler.ScheduleStatus;

// Schedule JPA repository — finders and overlap checks.
@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByBusId(Long busId);

    List<Schedule> findByRouteId(Long routeId);

    List<Schedule> findByStatus(ScheduleStatus status);

    List<Schedule> findByBusIdAndRouteId(Long busId, Long routeId);

    List<Schedule> findByBusIdAndStatus(Long busId, ScheduleStatus status);

    List<Schedule> findByRouteIdAndStatus(Long routeId, ScheduleStatus status);

    @Query("SELECT COUNT(s) > 0 FROM Schedule s WHERE s.busId = :busId " +
            "AND s.status = 'ACTIVE' " +
            "AND s.departTime < :arriveTime " +
            "AND s.arriveTime > :departTime")
    boolean existsOverlappingSchedule(
            @Param("busId") Long busId,
            @Param("departTime") LocalTime departTime,
            @Param("arriveTime") LocalTime arriveTime);

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
