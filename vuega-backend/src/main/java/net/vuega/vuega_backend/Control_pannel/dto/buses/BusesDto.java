package net.vuega.vuega_backend.Control_pannel.dto.buses;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.vuega.vuega_backend.Control_pannel.util.BusStatus;
import net.vuega.vuega_backend.Control_pannel.util.BusType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusesDto {

    private Long busId;

    @NotNull(message = "Operator ID is required")
    private Long operatorId;

    @NotBlank(message = "Bus number is required")
    private String busNumber;

    @NotNull(message = "Bus type is required")
    private BusType busType;

    @Min(value = 1, message = "Seat count must be greater than 0")
    private int seatCount;

    private Long layoutId;

    @NotNull(message = "Status is required")
    private BusStatus status;

}
