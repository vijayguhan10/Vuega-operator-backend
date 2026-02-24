package net.vuega.vuega_backend.DTO.seats.seat.bookings;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import net.vuega.vuega_backend.Model.seats.bookings.BookingStatus;

@Data
@Builder
public class BookingDTO {
    private Long seatStatusId;
    private Long seatId;
    private String seatNo;
    private Long busId;
    private Long scheduleId;
    private Long passengerId;
    private Integer fromStopOrder;
    private Integer toStopOrder;
    private BookingStatus status;
    private String idempotencyKey;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
