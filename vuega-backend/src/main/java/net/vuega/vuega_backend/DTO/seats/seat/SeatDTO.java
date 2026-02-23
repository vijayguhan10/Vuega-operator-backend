package net.vuega.vuega_backend.DTO.seats.seat;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.vuega.vuega_backend.Model.seats.seat.SeatStatus;
import net.vuega.vuega_backend.Model.seats.seat.SeatType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatDTO {

    private Long seatId;
    private Long busId;
    private String seatNo;
    private SeatType type;
    private BigDecimal price;
    private Integer fromStopOrder;
    private Integer toStopOrder;
    private SeatStatus status;
    private Object busDetails;
}
