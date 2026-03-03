error id: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/DTO/bookings/MultiSeatBookingRequest.java:jakarta/validation/constraints/Min#
file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/DTO/bookings/MultiSeatBookingRequest.java
empty definition using pc, found symbol in pc: jakarta/validation/constraints/Min#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 142
uri: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/DTO/bookings/MultiSeatBookingRequest.java
text:
```scala
package net.vuega.vuega_backend.DTO.bookings;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.@@Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MultiSeatBookingRequest {

    @NotNull(message = "sessionId is required")
    private Long sessionId;

    @NotNull(message = "partnerId is required")
    private Long partnerId;

    @NotEmpty(message = "passengerDetails must not be empty")
    @Valid
    private List<PassengerRequest> passengerDetails;

    @Size(max = 64, message = "idempotencyKey must be at most 64 characters")
    private String idempotencyKey;
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: jakarta/validation/constraints/Min#