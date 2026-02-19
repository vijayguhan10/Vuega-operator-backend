package net.vuega.vuega_backend.Model.seats;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "seats",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_bus_seat_no",
        columnNames = {"bus_id", "seat_no"}
    )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private Long seatId;

    // FK — references a bus managed by the Control Plane
    @Column(name = "bus_id", nullable = false)
    private Long busId;

    @Column(name = "seat_no", nullable = false, length = 10)
    private String seatNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 10)
    private SeatType type;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    // The route segment this seat is currently locked/booked for.
    // Null when AVAILABLE.
    @Column(name = "from_stop_order")
    private Integer fromStopOrder;

    @Column(name = "to_stop_order")
    private Integer toStopOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 12)
    @Builder.Default
    private SeatStatus status = SeatStatus.AVAILABLE;

    // ---- Lock metadata ------------------------------------------------

    /** Session-ID or user-ID that acquired the lock. */
    @Column(name = "locked_by", length = 100)
    private String lockedBy;

    /** Wall-clock time the lock was acquired; used for TTL expiry. */
    @Column(name = "locked_at")
    private LocalDateTime lockedAt;

    // ---- Optimistic locking -------------------------------------------

    /**
     * @Version causes Hibernate to append a WHERE version = ? clause on every
     * UPDATE. If a concurrent transaction already incremented the version,
     * Hibernate throws ObjectOptimisticLockingFailureException, preventing
     * silent lost updates (e.g., two threads both believing they booked the
     * same seat).
     */
    @Version
    @Column(name = "version", nullable = false)
    private Long version;
}
