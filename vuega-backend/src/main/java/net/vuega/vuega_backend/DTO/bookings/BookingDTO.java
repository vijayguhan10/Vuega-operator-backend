package net.vuega.vuega_backend.DTO.bookings;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import net.vuega.vuega_backend.Model.bookings.BookingStatus;

@Data
@Builder
public class BookingDTO {
    private Long bookingId;
    private Long seatId;
    private String seatNo;
    private Long busId;
    private Long scheduleId;
    private Long userId;
    private Integer fromStopOrder;
    private Integer toStopOrder;
    private BookingStatus status;
    private LocalDateTime bookedAt;
}
