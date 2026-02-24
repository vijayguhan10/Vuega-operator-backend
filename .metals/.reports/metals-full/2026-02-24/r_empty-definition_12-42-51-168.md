error id: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/DTO/seats/lock/BookSeatRequest.java:_empty_/Min#message#
file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/DTO/seats/lock/BookSeatRequest.java
empty definition using pc, found symbol in pc: _empty_/Min#message#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 620
uri: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/DTO/seats/lock/BookSeatRequest.java
text:
```scala
package net.vuega.vuega_backend.DTO.seats.lock;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BookSeatRequest {

    @NotNull(message = "partnerId is required")
    private Long partnerId;

    @NotNull(message = "scheduleId is required")
    private Long scheduleId;

    @NotNull(message = "fromStopOrder is required")
    @Min(value = 0, message = "fromStopOrder must be >= 0")
    private Integer fromStopOrder;

    @NotNull(message = "toStopOrder is required")
    @Min(value = 1, mess@@age = "toStopOrder must be >= 1")
    private Integer toStopOrder;

    @Size(max = 36, message = "idempotencyKey must be at most 36 characters")
    private String idempotencyKey;
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/Min#message#