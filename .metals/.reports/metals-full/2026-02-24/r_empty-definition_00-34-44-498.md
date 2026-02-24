error id: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Model/seats/lock/SeatLock.java:net/vuega/vuega_backend/Model/seats/seat/Seat#
file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Model/seats/lock/SeatLock.java
empty definition using pc, found symbol in pc: net/vuega/vuega_backend/Model/seats/seat/Seat#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 544
uri: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Model/seats/lock/SeatLock.java
text:
```scala
package net.vuega.vuega_backend.Model.seats.lock;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.vuega.vuega_backend.Model.seats.seat.@@Seat;

// JPA entity for the seat_locks table.
@Entity
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

    @Column(name = "partner_id", nullable = false)
    private Long partnerId;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: net/vuega/vuega_backend/Model/seats/seat/Seat#