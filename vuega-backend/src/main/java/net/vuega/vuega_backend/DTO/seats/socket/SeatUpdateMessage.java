package net.vuega.vuega_backend.DTO.seats.socket;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatUpdateMessage {

    public enum Event {
        LOCKED, UNLOCKED, BOOKED, CANCELLED, EXPIRED
    }

    private Event event;
    private Long seatId;
    private Long busId;
    private String seatNo;
    private Long scheduleId;
    private Integer fromStopOrder;
    private Integer toStopOrder;
    private LocalDateTime timestamp;
}
