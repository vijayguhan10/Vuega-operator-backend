error id: file://<WORKSPACE>/src/main/java/net/vuega/vuega_backend/Control_pannel/dto/expansionrequest/ExpansionRequestDto.java:net/vuega/vuega_backend/Control_pannel/util/ExpansionRequestFor#
file://<WORKSPACE>/src/main/java/net/vuega/vuega_backend/Control_pannel/dto/expansionrequest/ExpansionRequestDto.java
empty definition using pc, found symbol in pc: net/vuega/vuega_backend/Control_pannel/util/ExpansionRequestFor#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 298
uri: file://<WORKSPACE>/src/main/java/net/vuega/vuega_backend/Control_pannel/dto/expansionrequest/ExpansionRequestDto.java
text:
```scala
package net.vuega.vuega_backend.Control_pannel.dto.expansionrequest;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.vuega.vuega_backend.Control_pannel.util.@@ExpansionRequestFor;
import net.vuega.vuega_backend.Control_pannel.util.ExpansionRequestStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpansionRequestDto {

    private Long requestId;

    @NotNull(message = "Operator ID is required")
    private Long operatorId;

    @NotNull(message = "Request type is required")
    private ExpansionRequestFor requestFor;

    @NotNull(message = "Count is required")
    @Min(value = 1, message = "Count must be greater than 0")
    private Integer count;

    private ExpansionRequestStatus status;
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: net/vuega/vuega_backend/Control_pannel/util/ExpansionRequestFor#