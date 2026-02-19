package net.vuega.vuega_backend.DTO.seats;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.vuega.vuega_backend.Model.seats.SeatType;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateSeatRequest {

    /** Leave null to keep the current value. */
    @Size(max = 10, message = "seatNo must be at most 10 characters")
    private String seatNo;

    private SeatType type;

    @DecimalMin(value = "0.01", message = "price must be greater than 0")
    private BigDecimal price;
}
