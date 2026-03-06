package net.vuega.vuega_backend.Operator_pannel.DTO.seats.seat.bookings;

import lombok.Builder;
import lombok.Data;
import net.vuega.vuega_backend.Operator_pannel.Model.seats.bookings.BookingStatus;

@Data
@Builder
public class BookingDTO {
    private Long seatStatusId;
    private Long bookingId;
    private Long seatId;
    private String seatNo;
    private Long busId;
    private Long scheduleId;
    private Long passengerId;
    private Integer fromStopOrder;
    private Integer toStopOrder;
    private BookingStatus status;
}
