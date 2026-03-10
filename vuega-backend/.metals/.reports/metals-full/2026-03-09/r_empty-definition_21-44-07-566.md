error id: file://<WORKSPACE>/src/main/java/net/vuega/vuega_backend/Operator_pannel/DTO/bookings/PassengerRequest.java:net/vuega/vuega_backend/Operator_pannel/Model/passengers/Gender#
file://<WORKSPACE>/src/main/java/net/vuega/vuega_backend/Operator_pannel/DTO/bookings/PassengerRequest.java
empty definition using pc, found symbol in pc: net/vuega/vuega_backend/Operator_pannel/Model/passengers/Gender#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 285
uri: file://<WORKSPACE>/src/main/java/net/vuega/vuega_backend/Operator_pannel/DTO/bookings/PassengerRequest.java
text:
```scala
package net.vuega.vuega_backend.Operator_pannel.DTO.bookings;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.vuega.vuega_backend.Operator_pannel.Model.passengers.@@Gender;

@Data
public class PassengerRequest {

    @NotNull(message = "seatId is required")
    private Long seatId;

    @NotBlank(message = "name is required")
    private String name;

    @NotNull(message = "age is required")
    @Min(value = 0, message = "age must be >= 0")
    private Integer age;

    @NotNull(message = "gender is required")
    private Gender gender;

    @NotNull(message = "fromStopOrder is required")
    @Min(value = 0, message = "fromStopOrder must be >= 0")
    private Integer fromStopOrder;

    @NotNull(message = "toStopOrder is required")
    @Min(value = 1, message = "toStopOrder must be >= 1")
    private Integer toStopOrder;
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: net/vuega/vuega_backend/Operator_pannel/Model/passengers/Gender#