error id: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Model/seats/bookings/Booking.java:java/time/LocalDateTime#
file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Model/seats/bookings/Booking.java
empty definition using pc, found symbol in pc: java/time/LocalDateTime#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 2384
uri: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Model/seats/bookings/Booking.java
text:
```scala
package net.vuega.vuega_backend.Model.seats.bookings;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.vuega.vuega_backend.Model.seats.seat.Seat;

@Entity(name = "SeatBooking")
@Table(name = "seat_status")
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

    @Column(name = "passenger_id", nullable = false)
    private Long passengerId;

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

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTim@@e.now();
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: java/time/LocalDateTime#