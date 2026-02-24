package net.vuega.vuega_backend.Model.bookings;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.vuega.vuega_backend.Model.seats.seat.Seat;

@Entity
@Table(
    name = "bookings",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_booking_segment",
        columnNames = { "schedule_id", "seat_id", "from_stop_order", "to_stop_order" }
    ),
    indexes = @Index(name = "idx_booking_lookup", columnList = "schedule_id, seat_id")
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long bookingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "partner_id", nullable = false)
    private Long partnerId;

    @Column(name = "from_stop_order", nullable = false)
    private Integer fromStopOrder;

    @Column(name = "to_stop_order", nullable = false)
    private Integer toStopOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private BookingStatus status;

    @Column(name = "booked_at", nullable = false)
    private LocalDateTime bookedAt;
}
