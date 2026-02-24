package net.vuega.vuega_backend.DTO.seats.lock;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SeatLockDTO {
    private Long lockId;
    private Long seatId;
    private String seatNo;
    private Long busId;
    private Long scheduleId;
    private Long passengerId;
    private LocalDateTime expiresAt;
}
