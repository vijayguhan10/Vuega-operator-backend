error id: file://<WORKSPACE>/src/main/java/net/vuega/vuega_backend/Operator_pannel/DTO/bookings/MultiSeatBookingResponse.java:net/vuega/vuega_backend/Operator_pannel/Model/bookings/BookingStatus#
file://<WORKSPACE>/src/main/java/net/vuega/vuega_backend/Operator_pannel/DTO/bookings/MultiSeatBookingResponse.java
empty definition using pc, found symbol in pc: net/vuega/vuega_backend/Operator_pannel/Model/bookings/BookingStatus#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 479
uri: file://<WORKSPACE>/src/main/java/net/vuega/vuega_backend/Operator_pannel/DTO/bookings/MultiSeatBookingResponse.java
text:
```scala
package net.vuega.vuega_backend.Operator_pannel.DTO.bookings;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.vuega.vuega_backend.Operator_pannel.DTO.passengers.PassengerDTO;
import net.vuega.vuega_backend.Operator_pannel.DTO.seats.seat.bookings.BookingDTO;
import net.vuega.vuega_backend.Operator_pannel.Model.bookings.@@BookingStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MultiSeatBookingResponse {
    private Long bookingId;
    private String pnr;
    private Long partnerId;
    private Long scheduleId;
    private BookingStatus status;
    private BigDecimal totalAmount;
    private String idempotencyKey;
    private LocalDateTime createdAt;
    private List<PassengerDTO> passengers;
    private List<BookingDTO> seatBookings;
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: net/vuega/vuega_backend/Operator_pannel/Model/bookings/BookingStatus#