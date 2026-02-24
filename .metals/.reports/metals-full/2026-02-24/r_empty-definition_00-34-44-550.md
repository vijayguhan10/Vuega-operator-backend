error id: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/DTO/seats/socket/SeatUpdateMessage.java:net/vuega/vuega_backend/Model/seats/seat/SeatStatus#
file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/DTO/seats/socket/SeatUpdateMessage.java
empty definition using pc, found symbol in pc: net/vuega/vuega_backend/Model/seats/seat/SeatStatus#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 242
uri: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/DTO/seats/socket/SeatUpdateMessage.java
text:
```scala
package net.vuega.vuega_backend.DTO.seats.socket;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.vuega.vuega_backend.Model.seats.seat.@@SeatStatus;

// Payload broadcast to /topic/seats/bus/{busId} on every seat state change.
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatUpdateMessage {

    public enum Event {
        LOCKED, UNLOCKED, BOOKED, CANCELLED, EXPIRED
    }

    private Event event;
    private Long seatId; // null for bulk EXPIRED messages
    private Long busId; // null for bulk EXPIRED messages
    private String seatNo; // null for bulk EXPIRED messages
    private SeatStatus status;
    private int count; // 1 for individual events; N for bulk EXPIRED
    private LocalDateTime timestamp;
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: net/vuega/vuega_backend/Model/seats/seat/SeatStatus#