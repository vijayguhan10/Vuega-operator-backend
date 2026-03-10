error id: file://<WORKSPACE>/src/main/java/net/vuega/vuega_backend/Control_pannel/dto/routes/RouteDto.java:net/vuega/vuega_backend/Control_pannel/util/RouteStatus#
file://<WORKSPACE>/src/main/java/net/vuega/vuega_backend/Control_pannel/dto/routes/RouteDto.java
empty definition using pc, found symbol in pc: net/vuega/vuega_backend/Control_pannel/util/RouteStatus#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 198
uri: file://<WORKSPACE>/src/main/java/net/vuega/vuega_backend/Control_pannel/dto/routes/RouteDto.java
text:
```scala
package net.vuega.vuega_backend.Control_pannel.dto.routes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.vuega.vuega_backend.Control_pannel.util.@@RouteStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteDto {

    private Long routeId;
    private Long operatorId;
    private Long fromCityId;
    private Long toCityId;
    private Integer totalDistance;
    private RouteStatus status;
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: net/vuega/vuega_backend/Control_pannel/util/RouteStatus#