error id: file://<WORKSPACE>/src/main/java/net/vuega/vuega_backend/Control_pannel/repository/licenselimits/LicenseLimitsRepository.java:net/vuega/vuega_backend/Control_pannel/model/licenselimits/LicenseLimits#
file://<WORKSPACE>/src/main/java/net/vuega/vuega_backend/Control_pannel/repository/licenselimits/LicenseLimitsRepository.java
empty definition using pc, found symbol in pc: net/vuega/vuega_backend/Control_pannel/model/licenselimits/LicenseLimits#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 253
uri: file://<WORKSPACE>/src/main/java/net/vuega/vuega_backend/Control_pannel/repository/licenselimits/LicenseLimitsRepository.java
text:
```scala
package net.vuega.vuega_backend.Control_pannel.repository.licenselimits;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.vuega.vuega_backend.Control_pannel.model.licenselimits.@@LicenseLimits;

@Repository
public interface LicenseLimitsRepository extends JpaRepository<LicenseLimits, Long> {
    LicenseLimits findByLicenseId(long licenseId);

}

```


#### Short summary: 

empty definition using pc, found symbol in pc: net/vuega/vuega_backend/Control_pannel/model/licenselimits/LicenseLimits#