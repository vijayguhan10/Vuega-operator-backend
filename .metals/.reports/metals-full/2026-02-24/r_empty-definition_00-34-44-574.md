error id: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/DTO/seats/seat/UpdateSeatRequest.java:net/vuega/vuega_backend/Model/seats/seat/SeatType#
file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/DTO/seats/seat/UpdateSeatRequest.java
empty definition using pc, found symbol in pc: net/vuega/vuega_backend/Model/seats/seat/SeatType#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 331
uri: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/DTO/seats/seat/UpdateSeatRequest.java
text:
```scala
package net.vuega.vuega_backend.DTO.seats.seat;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.vuega.vuega_backend.Model.seats.seat.@@SeatType;

// Request DTO for partial seat update — null fields are ignored.
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateSeatRequest {

    @Size(max = 10, message = "seatNo must be at most 10 characters")
    private String seatNo;

    private SeatType type;

    @DecimalMin(value = "0.01", message = "price must be greater than 0")
    private BigDecimal price;
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: net/vuega/vuega_backend/Model/seats/seat/SeatType#