error id: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Service/seats/SeatSocketService.java:net/vuega/vuega_backend/DTO/seats/SeatUpdateMessage#
file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Service/seats/SeatSocketService.java
empty definition using pc, found symbol in pc: net/vuega/vuega_backend/DTO/seats/SeatUpdateMessage#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 275
uri: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Service/seats/SeatSocketService.java
text:
```scala
package net.vuega.vuega_backend.Service.seats;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.vuega.vuega_backend.DTO.seats.@@SeatUpdateMessage;

// Broadcasts real-time seat state changes to /topic/seats.
@Service
@RequiredArgsConstructor
@Slf4j
public class SeatSocketService {

    private static final String SEAT_TOPIC = "/topic/seats";

    private final SimpMessagingTemplate messagingTemplate;

    public void broadcast(SeatUpdateMessage message) {
        messagingTemplate.convertAndSend(SEAT_TOPIC, message);
        log.debug("[SeatSocket] {} → seatId={} status={} count={}",
                message.getEvent(), message.getSeatId(),
                message.getStatus(), message.getCount());
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: net/vuega/vuega_backend/DTO/seats/SeatUpdateMessage#