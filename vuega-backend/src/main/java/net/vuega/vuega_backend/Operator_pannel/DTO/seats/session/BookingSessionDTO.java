package net.vuega.vuega_backend.Operator_pannel.DTO.seats.session;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingSessionDTO {
    private Long sessionId;
    private Long scheduleId;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
}
