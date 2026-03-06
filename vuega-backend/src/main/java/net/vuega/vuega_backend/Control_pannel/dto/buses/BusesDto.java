package net.vuega.vuega_backend.Control_pannel.dto.buses;

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
    private Long operatorId;
    private String busNumber;
    private BusType busType;
    private int seatCount;
    private Long layoutId;
    private BusStatus status;

}
