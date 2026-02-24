error id: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Repository/bookings/BookingRepository.java:net/vuega/vuega_backend/Model/bookings/Booking#
file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Repository/bookings/BookingRepository.java
empty definition using pc, found symbol in pc: net/vuega/vuega_backend/Model/bookings/Booking#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 323
uri: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Repository/bookings/BookingRepository.java
text:
```scala
package net.vuega.vuega_backend.Repository.bookings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import net.vuega.vuega_backend.Model.bookings.@@Booking;
import net.vuega.vuega_backend.Model.bookings.BookingStatus;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
            SELECT COUNT(b) FROM Booking b
            WHERE b.seat.seatId = :seatId
            AND b.scheduleId = :scheduleId
            AND b.status = :status
            AND b.fromStopOrder < :toStop
            AND b.toStopOrder > :fromStop
            """)
    long countOverlappingBookings(
            @Param("seatId") Long seatId,
            @Param("scheduleId") Long scheduleId,
            @Param("fromStop") int fromStop,
            @Param("toStop") int toStop,
            @Param("status") BookingStatus status);
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: net/vuega/vuega_backend/Model/bookings/Booking#