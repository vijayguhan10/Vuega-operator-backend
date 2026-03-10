error id: file://<WORKSPACE>/src/main/java/net/vuega/vuega_backend/Control_pannel/dto/buses/BusesDto.java:net/vuega/vuega_backend/Control_pannel/util/BusStatus#
file://<WORKSPACE>/src/main/java/net/vuega/vuega_backend/Control_pannel/dto/buses/BusesDto.java
empty definition using pc, found symbol in pc: net/vuega/vuega_backend/Control_pannel/util/BusStatus#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 197
uri: file://<WORKSPACE>/src/main/java/net/vuega/vuega_backend/Control_pannel/dto/buses/BusesDto.java
text:
```scala
package net.vuega.vuega_backend.Control_pannel.dto.buses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.vuega.vuega_backend.Control_pannel.util.@@BusStatus;
import net.vuega.vuega_backend.Control_pannel.util.BusType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusesDto {

    private Long busId;
    private Long operatorId;
    private String busNumber;
    private BusType busType;
    private int seatCount;
    private Long layoutId;
    private BusStatus status;

}

```


#### Short summary: 

empty definition using pc, found symbol in pc: net/vuega/vuega_backend/Control_pannel/util/BusStatus#