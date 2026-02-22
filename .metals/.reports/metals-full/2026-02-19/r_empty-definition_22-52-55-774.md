error id: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/DTO/seats/CreateSeatRequest.java:_empty_/Builder#
file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/DTO/seats/CreateSeatRequest.java
empty definition using pc, found symbol in pc: _empty_/Builder#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 290
uri: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/DTO/seats/CreateSeatRequest.java
text:
```scala
package net.vuega.vuega_backend.DTO.seats;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.vuega.vuega_backend.Model.seats.SeatType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builde@@r
public class CreateSeatRequest {

    @NotNull(message = "busId is required")
    private Long busId;

    @NotBlank(message = "seatNo is required")
    @Size(max = 10, message = "seatNo must be at most 10 characters")
    private String seatNo;

    @NotNull(message = "type is required (SEATER or SLEEPER)")
    private SeatType type;

    @NotNull(message = "price is required")
    @DecimalMin(value = "0.01", message = "price must be greater than 0")
    private BigDecimal price;
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/Builder#