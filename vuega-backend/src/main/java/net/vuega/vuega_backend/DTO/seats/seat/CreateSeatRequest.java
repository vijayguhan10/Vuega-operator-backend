package net.vuega.vuega_backend.DTO.seats.seat;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.vuega.vuega_backend.Model.seats.seat.SeatType;

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

    @NotNull(message = "fromStopOrder is required")
    @Min(value = 0, message = "fromStopOrder must be >= 0")
    private Integer fromStopOrder;

    @NotNull(message = "toStopOrder is required")
    @Min(value = 1, message = "toStopOrder must be >= 1")
    private Integer toStopOrder;
}
