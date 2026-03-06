package net.vuega.vuega_backend.Operator_pannel.Service.seats.socket;

import java.time.LocalDateTime;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.vuega.vuega_backend.Operator_pannel.DTO.seats.socket.SeatUpdateMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatSocketService {

    private static final String BUS_TOPIC_PREFIX = "/topic/seats/bus/";

    private final SimpMessagingTemplate messagingTemplate;

    // Sends a seat update event to the WebSocket topic for the given bus.
    public void broadcast(SeatUpdateMessage message) {
        if (message.getTimestamp() == null) {
            message.setTimestamp(LocalDateTime.now());
        }

        String topic = BUS_TOPIC_PREFIX + message.getBusId();

        messagingTemplate.convertAndSend(topic, message);

        log.info("[WebSocket] {} → bus={} seat={} segment={}-{}",
                message.getEvent(),
                message.getBusId(),
                message.getSeatNo(),
                message.getFromStopOrder(),
                message.getToStopOrder());
    }
}
