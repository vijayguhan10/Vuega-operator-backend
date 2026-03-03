error id: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Repository/seats/lock/SeatLockRepository.java:_empty_/SeatLock#
file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Repository/seats/lock/SeatLockRepository.java
empty definition using pc, found symbol in pc: _empty_/SeatLock#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 814
uri: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Repository/seats/lock/SeatLockRepository.java
text:
```scala
package net.vuega.vuega_backend.Repository.seats.lock;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import net.vuega.vuega_backend.Model.seats.lock.SeatLock;

@Repository
public interface SeatLockRepository extends JpaRepository<SeatLock, Long> {

        /**
         * Find a lock by seat and schedule (unique constraint ensures at most one).
         */
        @Query("""
                        SELECT l FROM SeatLock l
                        WHERE l.seat.seatId = :seatId
                        AND l.scheduleId = :scheduleId
                        """)
        Optional<Seat@@Lock> findBySeatIdAndScheduleId(
                        @Param("seatId") Long seatId,
                        @Param("scheduleId") Long scheduleId);

        /**
         * Fetch all locks belonging to a session, eagerly loading seat data.
         */
        @Query("SELECT l FROM SeatLock l JOIN FETCH l.seat WHERE l.session.sessionId = :sessionId")
        List<SeatLock> findBySessionIdWithSeat(@Param("sessionId") Long sessionId);
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/SeatLock#