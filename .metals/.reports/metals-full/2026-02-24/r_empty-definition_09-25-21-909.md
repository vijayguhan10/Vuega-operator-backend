error id: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Service/seats/socket/SeatSocketService.java:SeatUpdateMessage/Event#SYNC#
file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Service/seats/socket/SeatSocketService.java
empty definition using pc, found symbol in pc: SeatUpdateMessage/Event#SYNC#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 1704
uri: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Service/seats/socket/SeatSocketService.java
text:
```scala
package net.vuega.vuega_backend.Service.seats.socket;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.vuega.vuega_backend.DTO.seats.seat.SeatDTO;
import net.vuega.vuega_backend.DTO.seats.socket.SeatUpdateMessage;
import net.vuega.vuega_backend.Repository.seats.seat.SeatRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatSocketService {

    private static final String BUS_TOPIC_PREFIX = "/topic/seats/bus/";
    private static final String GLOBAL_TOPIC = "/topic/seats/global";

    private final SimpMessagingTemplate messagingTemplate;
    private final SeatRepository seatRepository;

    @Scheduled(fixedRate = 2000)
    public void broadcastAllSeatsStatus() {
        List<SeatDTO> allSeats = seatRepository.findAll().stream()
                .map(seat -> SeatDTO.builder()
                        .seatId(seat.getSeatId())
                        .busId(seat.getBusId())
                        .seatNo(seat.getSeatNo())
                        .status(seat.getStatus())
                        .build())
                .collect(Collectors.toList());

        Map<Long, List<SeatDTO>> seatsByBus = allSeats.stream()
                .collect(Collectors.groupingBy(SeatDTO::getBusId));

        seatsByBus.forEach((busId, seats) -> {
            SeatUpdateMessage message = SeatUpdateMessage.builder()
                    .event(SeatUpdateMessage.Event.@@SYNC)
                    .busId(busId)
                    .seats(seats)
                    .count(seats.size())
                    .timestamp(LocalDateTime.now())
                    .build();
            messagingTemplate.convertAndSend(BUS_TOPIC_PREFIX + busId, message);
        });

        log.debug("[SeatSocket] Broadcasted {} seats across {} buses", allSeats.size(), seatsByBus.size());
    }

    public void broadcast(SeatUpdateMessage message) {
        String topic = message.getBusId() != null
                ? BUS_TOPIC_PREFIX + message.getBusId()
                : GLOBAL_TOPIC;
        messagingTemplate.convertAndSend(topic, message);
        log.debug("[SeatSocket] {} \u2192 bus={} seat={} status={}",
                message.getEvent(), message.getBusId(),
                message.getSeatId(), message.getStatus());
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: SeatUpdateMessage/Event#SYNC#