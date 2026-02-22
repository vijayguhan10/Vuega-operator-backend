package net.vuega.vuega_backend.DTO.seats;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.vuega.vuega_backend.Model.seats.SeatStatus;
import net.vuega.vuega_backend.Model.seats.SeatType;

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

    private String lockedBy;

    private LocalDateTime lockedAt;

    private Object busDetails;
}
