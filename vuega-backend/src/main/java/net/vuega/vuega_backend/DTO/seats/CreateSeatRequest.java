package net.vuega.vuega_backend.DTO.seats;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.vuega.vuega_backend.Model.seats.SeatType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSeatRequest {

    @NotNull(message = "busId is required")
    private Long busId;

    @NotBlank(message = "seatNo is required")
    @Size(max = 10, message = "seatNo must be at most 10 characters")
    private String seatNo;

    @NotNull(message = "type is required (SEATER or SLEEPER)")
    private SeatType type;

    @NotNull(message = "price is required")
    @DecimalMin(value = "0.01", message = "price must be greater than 0")
    private BigDecimal price;
}
