package net.vuega.vuega_backend.DTO.scheduler;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.vuega.vuega_backend.Model.scheduler.ScheduleStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleDTO {

    private Long scheduleId;
    private Long busId;
    private Long routeId;
    private LocalTime departTime;
    private LocalTime arriveTime;
    private ScheduleStatus status;

    // Enriched fields from Control Plane (bus & route details)
    private Object busDetails;
    private Object routeDetails;
}
