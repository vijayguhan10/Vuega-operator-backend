package net.vuega.vuega_backend.DTO.bookings;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.vuega.vuega_backend.Model.bookings.BookingStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDTO {

    private Long bookingId;
    private String pnr;
    private Long partnerId;
    private Long scheduleId;
    private BookingStatus status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
}
