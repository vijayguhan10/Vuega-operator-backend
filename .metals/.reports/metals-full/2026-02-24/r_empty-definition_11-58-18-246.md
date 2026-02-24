error id: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Model/bookings/Booking.java:jakarta/persistence/EntityListeners#
file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Model/bookings/Booking.java
empty definition using pc, found symbol in pc: jakarta/persistence/EntityListeners#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 372
uri: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Model/bookings/Booking.java
text:
```scala
package net.vuega.vuega_backend.Model.bookings;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.@@EntityListeners;
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
@Table(name = "seat_status", uniqueConstraints = @UniqueConstraint(name = "uq_seat_status_segment", columnNames = {
        "schedule_id", "seat_id", "from_stop_order",
        "to_stop_order" }), indexes = @Index(name = "idx_seat_status_lookup", columnList = "schedule_id, seat_id"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_status_id")
    private Long seatStatusId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;

    @Column(name = "partner_id", nullable = false)
    private Long partnerId;

    @Column(name = "idempotency_key", unique = true, nullable = true, length = 36)
    private String idempotencyKey;

    @Column(name = "from_stop_order", nullable = false)
    private Integer fromStopOrder;

    @Column(name = "to_stop_order", nullable = false)
    private Integer toStopOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private BookingStatus status;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: jakarta/persistence/EntityListeners#