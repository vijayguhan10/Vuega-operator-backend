error id: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Service/seats/socket/SeatSocketService.java:_empty_/SeatUpdateMessage#getEvent#
file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Service/seats/socket/SeatSocketService.java
empty definition using pc, found symbol in pc: _empty_/SeatUpdateMessage#getEvent#
found definition using semanticdb; symbol net/vuega/vuega_backend/Service/seats/socket/SeatSocketService#broadcastAllSeatsStatus().
empty definition using fallback
non-local guesses:

offset: 1161
uri: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Service/seats/socket/SeatSocketService.java
text:
```scala
package net.vuega.vuega_backend.Service.seats.socket;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.vuega.vuega_backend.DTO.seats.socket.SeatUpdateMessage;

// Routes real-time seat events to /topic/seats/bus/{busId} — one topic per bus.
@Service
@RequiredArgsConstructor
@Slf4j
public class SeatSocketService {

    // Per-bus topic; busId is appended at call time.
    private static final String BUS_TOPIC_PREFIX = "/topic/seats/bus/";
    // Fallback for bulk events that have no specific busId (e.g., scheduled
    // expiry).
    private static final String GLOBAL_TOPIC = "/topic/seats/global";

    private final SimpMessagingTemplate messagingTemplate;

    public void broadcast(SeatUpdateMessage message) {
        String topic = message.getBusId() != null
                ? BUS_TOPIC_PREFIX + message.getBusId()
                : GLOBAL_TOPIC;
        messagingTemplate.convertAndSend(topic, message);
        log.debug("[SeatSocket] {} \u2192 bus={} seat={} status={}",
                message.ge@@tEvent(), message.getBusId(),
                message.getSeatId(), message.getStatus());
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/SeatUpdateMessage#getEvent#