error id: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/DTO/seats/lock/AcquireLockRequest.java:jakarta/validation/constraints/NotNull#
file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/DTO/seats/lock/AcquireLockRequest.java
empty definition using pc, found symbol in pc: jakarta/validation/constraints/NotNull#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 87
uri: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/DTO/seats/lock/AcquireLockRequest.java
text:
```scala
package net.vuega.vuega_backend.DTO.seats.lock;

import jakarta.validation.constraints.@@NotNull;
import lombok.Data;

@Data
public class AcquireLockRequest {

    @NotNull(message = "partnerId is required")
    private Long partnerId;

    @NotNull(message = "scheduleId is required")
    private Long scheduleId;
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: jakarta/validation/constraints/NotNull#