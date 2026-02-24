package net.vuega.vuega_backend.Model.seats.seat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "seats", uniqueConstraints = @UniqueConstraint(name = "uq_bus_seat_no", columnNames = { "bus_id",
        "seat_no" }))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private Long seatId;

    @Column(name = "bus_id", nullable = false)
    private Long busId;

    @Column(name = "seat_no", nullable = false, length = 10)
    private String seatNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 10)
    private SeatType type;

    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
