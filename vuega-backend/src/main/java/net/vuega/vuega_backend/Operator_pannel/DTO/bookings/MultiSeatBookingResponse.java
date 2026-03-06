package net.vuega.vuega_backend.DTO.bookings;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.vuega.vuega_backend.DTO.passengers.PassengerDTO;
import net.vuega.vuega_backend.DTO.seats.seat.bookings.BookingDTO;
import net.vuega.vuega_backend.Model.bookings.BookingStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MultiSeatBookingResponse {
    private Long bookingId;
    private String pnr;
    private Long partnerId;
    private Long scheduleId;
    private BookingStatus status;
    private BigDecimal totalAmount;
    private String idempotencyKey;
    private LocalDateTime createdAt;
    private List<PassengerDTO> passengers;
    private List<BookingDTO> seatBookings;
}
