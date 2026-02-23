package net.vuega.vuega_backend.DTO.seats.lock;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatLockDTO {

    private Long lockId;
    private Long seatId;
    private String seatNo;
    private Long busId;
    private Long partnerId;
    private LocalDateTime expiresAt;
}
