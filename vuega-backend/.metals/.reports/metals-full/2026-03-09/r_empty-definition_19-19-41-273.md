error id: file://<WORKSPACE>/src/main/java/net/vuega/vuega_backend/Operator_pannel/Service/operatorconfig/OperatorService.java:net/vuega/vuega_backend/Operator_pannel/DTO/operatorconfig/OperatorDTO#
file://<WORKSPACE>/src/main/java/net/vuega/vuega_backend/Operator_pannel/Service/operatorconfig/OperatorService.java
empty definition using pc, found symbol in pc: net/vuega/vuega_backend/Operator_pannel/DTO/operatorconfig/OperatorDTO#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 395
uri: file://<WORKSPACE>/src/main/java/net/vuega/vuega_backend/Operator_pannel/Service/operatorconfig/OperatorService.java
text:
```scala
package net.vuega.vuega_backend.Operator_pannel.Service.operatorconfig;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import net.vuega.vuega_backend.Operator_pannel.DTO.operatorconfig.LoginRequest;
import net.vuega.vuega_backend.Operator_pannel.DTO.operatorconfig.@@OperatorDTO;
import net.vuega.vuega_backend.Operator_pannel.Service.cache.ControlPanelCacheService;

@Service
public class OperatorService {

    private final ControlPanelCacheService cacheService;
    private final BCryptPasswordEncoder passwordEncoder;

    public OperatorService(ControlPanelCacheService cacheService) {
        this.cacheService = cacheService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // Validates login credentials against cached OperatorAuth data in Redis.
    public OperatorDTO login(LoginRequest request) {
        Map<String, Object> authData = cacheService.getAuthByEmail(request.getEmail());
        if (authData.isEmpty()) {
            return null;
        }

        String storedPassword = (String) authData.get("password");
        if (storedPassword == null || !passwordEncoder.matches(request.getPassword(), storedPassword)) {
            return null;
        }

        Long operatorId = ((Number) authData.get("operatorId")).longValue();
        Map<String, Object> operatorData = cacheService.getOperatorDetails(operatorId);
        Map<String, Object> licenseData = cacheService.getLicenseByOperator(operatorId);

        String operatorName = operatorData.containsKey("operatorName")
                ? (String) operatorData.get("operatorName") : null;
        String companyName = operatorData.containsKey("companyName")
                ? (String) operatorData.get("companyName") : null;
        String accountStatus = operatorData.containsKey("status")
                ? (String) operatorData.get("status") : null;
        String licenseStatus = licenseData.containsKey("status")
                ? (String) licenseData.get("status") : null;

        Integer busLimit = null;
        Integer routeLimit = null;
        if (!licenseData.isEmpty() && licenseData.containsKey("licenseId")) {
            Long licenseId = ((Number) licenseData.get("licenseId")).longValue();
            Map<String, Object> limitsData = cacheService.getLicenseLimitsByLicenseId(licenseId);
            if (!limitsData.isEmpty()) {
                busLimit = limitsData.containsKey("busLimit")
                        ? ((Number) limitsData.get("busLimit")).intValue() : null;
                routeLimit = limitsData.containsKey("routeLimit")
                        ? ((Number) limitsData.get("routeLimit")).intValue() : null;
            }
        }

        return OperatorDTO.builder()
                .operatorId(operatorId)
                .serviceEmail(request.getEmail())
                .operatorName(operatorName)
                .organizationName(companyName)
                .licenseStatus(licenseStatus)
                .accountStatus(accountStatus)
                .busLimit(busLimit)
                .routeLimit(routeLimit)
                .lastChecked(LocalDateTime.now())
                .build();
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: net/vuega/vuega_backend/Operator_pannel/DTO/operatorconfig/OperatorDTO#