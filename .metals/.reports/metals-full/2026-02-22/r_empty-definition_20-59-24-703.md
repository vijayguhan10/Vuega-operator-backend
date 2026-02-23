error id: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/DTO/seats/LockSeatRequest.java:Builder#
file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/DTO/seats/LockSeatRequest.java
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 390
uri: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/DTO/seats/LockSeatRequest.java
text:
```scala
package net.vuega.vuega_backend.DTO.seats;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder@@
public class LockSeatRequest {

    @NotBlank(message = "lockedBy (session or user ID) is required")
    @Size(max = 100, message = "lockedBy must be at most 100 characters")
    private String lockedBy;

    @NotNull(message = "fromStopOrder is required")
    @Min(value = 0, message = "fromStopOrder must be >= 0")
    private Integer fromStopOrder;

    @NotNull(message = "toStopOrder is required")
    @Min(value = 1, message = "toStopOrder must be >= 1")
    private Integer toStopOrder;
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: 