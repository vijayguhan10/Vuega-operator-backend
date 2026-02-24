error id: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/DTO/seats/socket/SeatUpdateMessage.java:java/util/List#
file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/DTO/seats/socket/SeatUpdateMessage.java
empty definition using pc, found symbol in pc: java/util/List#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 100
uri: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/DTO/seats/socket/SeatUpdateMessage.java
text:
```scala
package net.vuega.vuega_backend.DTO.seats.socket;

import java.time.LocalDateTime;
import java.util.@@List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.vuega.vuega_backend.DTO.seats.seat.SeatDTO;
import net.vuega.vuega_backend.Model.seats.seat.SeatStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatUpdateMessage {

    public enum Event {
        LOCKED, UNLOCKED, BOOKED, CANCELLED, EXPIRED, SYNC
    }

    private Event event;
    private Long seatId;
    private Long busId;
    private String seatNo;
    private SeatStatus status;
    private int count;
    private LocalDateTime timestamp;
    private List<SeatDTO> seats;
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: java/util/List#