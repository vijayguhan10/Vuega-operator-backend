package net.vuega.vuega_backend.Model.scheduler;

import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "schedules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long scheduleId;

    @Column(name = "bus_id", nullable = false)
    private Long busId;

    @Column(name = "route_id", nullable = false)
    private Long routeId;

    @Column(name = "depart_time", nullable = false)
    private LocalTime departTime;

    @Column(name = "arrive_time", nullable = false)
    private LocalTime arriveTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ScheduleStatus status = ScheduleStatus.ACTIVE;
}
