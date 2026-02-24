package net.vuega.vuega_backend.DTO.bookings;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.vuega.vuega_backend.Model.bookings.BookingStatus;

@Data
public class CreateBookingRequest {

    @NotBlank
    private String pnr;

    @NotNull
    private Long partnerId;

    @NotNull
    private Long scheduleId;

    @NotNull
    private BookingStatus status;

    @NotNull
    private BigDecimal totalAmount;
}
