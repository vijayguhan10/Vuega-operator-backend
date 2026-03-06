package net.vuega.vuega_backend.DTO.scheduler;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.vuega.vuega_backend.Model.scheduler.ScheduleStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateScheduleRequest {

    private Long busId;
    private Long routeId;
    private LocalTime departTime;
    private LocalTime arriveTime;
    private ScheduleStatus status;
}
