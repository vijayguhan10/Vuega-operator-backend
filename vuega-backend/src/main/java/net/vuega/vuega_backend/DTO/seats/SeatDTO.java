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

    /**
     * The stop segment this seat is currently locked/booked for. Null when
     * AVAILABLE.
     */
    private Integer fromStopOrder;
    private Integer toStopOrder;

    private SeatStatus status;

    /**
     * Session/user that holds the current lock. Null after booking or when
     * AVAILABLE.
     */
    private String lockedBy;

    /**
     * Wall-clock time the lock was acquired. Null after booking or when AVAILABLE.
     */
    private LocalDateTime lockedAt;

    /** Enriched: bus details fetched from the Control Plane. */
    private Object busDetails;
}
