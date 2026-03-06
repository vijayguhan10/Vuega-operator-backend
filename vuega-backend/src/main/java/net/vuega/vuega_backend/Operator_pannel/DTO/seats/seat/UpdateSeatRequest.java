package net.vuega.vuega_backend.DTO.seats.seat;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.vuega.vuega_backend.Model.seats.seat.SeatType;

// Request DTO for partial seat update — null fields are ignored.
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateSeatRequest {

    @Size(max = 10, message = "seatNo must be at most 10 characters")
    private String seatNo;

    private SeatType type;

    @DecimalMin(value = "0.01", message = "basePrice must be greater than 0")
    private BigDecimal basePrice;
}
