package net.vuega.vuega_backend.Model.seats.lock;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "seat_locks", uniqueConstraints = @UniqueConstraint(name = "uq_lock_schedule_seat", columnNames = {
        "schedule_id", "seat_id",
        "partner_id" }), indexes = @Index(name = "idx_lock_lookup", columnList = "schedule_id, seat_id"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatLock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lock_id")
    private Long lockId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;

    @Column(name = "partner_id", nullable = false)
    private Long partnerId;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
}
