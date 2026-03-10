error id: file://<WORKSPACE>/src/main/java/net/vuega/vuega_backend/Operator_pannel/DTO/seats/seat/CreateSeatRequest.java:net/vuega/vuega_backend/Operator_pannel/Model/seats/seat/SeatType#
file://<WORKSPACE>/src/main/java/net/vuega/vuega_backend/Operator_pannel/DTO/seats/seat/CreateSeatRequest.java
empty definition using pc, found symbol in pc: net/vuega/vuega_backend/Operator_pannel/Model/seats/seat/SeatType#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 458
uri: file://<WORKSPACE>/src/main/java/net/vuega/vuega_backend/Operator_pannel/DTO/seats/seat/CreateSeatRequest.java
text:
```scala
package net.vuega.vuega_backend.Operator_pannel.DTO.seats.seat;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.vuega.vuega_backend.Operator_pannel.Model.seats.seat.@@SeatType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSeatRequest {

    @NotNull(message = "busId is required")
    private Long busId;

    @NotBlank(message = "seatNo is required")
    @Size(max = 10, message = "seatNo must be at most 10 characters")
    private String seatNo;

    @NotNull(message = "type is required (SEATER or SLEEPER)")
    private SeatType type;

    @NotNull(message = "basePrice is required")
    @DecimalMin(value = "0.01", message = "basePrice must be greater than 0")
    private BigDecimal basePrice;
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: net/vuega/vuega_backend/Operator_pannel/Model/seats/seat/SeatType#