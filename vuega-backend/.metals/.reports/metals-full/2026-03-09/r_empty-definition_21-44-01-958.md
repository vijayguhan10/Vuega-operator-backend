error id: file://<WORKSPACE>/src/main/java/net/vuega/vuega_backend/Control_pannel/dto/licenses/LicensesDto.java:net/vuega/vuega_backend/Control_pannel/util/LicenseStatus#
file://<WORKSPACE>/src/main/java/net/vuega/vuega_backend/Control_pannel/dto/licenses/LicensesDto.java
empty definition using pc, found symbol in pc: net/vuega/vuega_backend/Control_pannel/util/LicenseStatus#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 224
uri: file://<WORKSPACE>/src/main/java/net/vuega/vuega_backend/Control_pannel/dto/licenses/LicensesDto.java
text:
```scala
package net.vuega.vuega_backend.Control_pannel.dto.licenses;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.vuega.vuega_backend.Control_pannel.util.@@LicenseStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LicensesDto {

    private Long licenseId;
    private Long operatorId;
    private String licenseKey;
    private Date startDate;
    private Date endDate;
    private LicenseStatus status;
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: net/vuega/vuega_backend/Control_pannel/util/LicenseStatus#