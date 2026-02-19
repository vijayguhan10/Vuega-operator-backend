error id: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/DTO/scheduler/ScheduleDTO.java:java/lang/Object#
file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/DTO/scheduler/ScheduleDTO.java
empty definition using pc, found symbol in pc: java/lang/Object#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 592
uri: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/DTO/scheduler/ScheduleDTO.java
text:
```scala
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
    private @@Object busDetails;
    private Object routeDetails;
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: java/lang/Object#