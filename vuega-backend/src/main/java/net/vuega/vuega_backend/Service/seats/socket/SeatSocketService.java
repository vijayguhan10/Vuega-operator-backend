package net.vuega.vuega_backend.Service.seats.socket;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.vuega.vuega_backend.DTO.seats.socket.SeatUpdateMessage;

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
