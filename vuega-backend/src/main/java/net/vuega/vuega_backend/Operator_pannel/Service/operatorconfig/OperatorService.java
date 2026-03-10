package net.vuega.vuega_backend.Operator_pannel.Service.operatorconfig;

import java.time.LocalDateTime;
import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import net.vuega.vuega_backend.Operator_pannel.DTO.operatorconfig.LoginRequest;
import net.vuega.vuega_backend.Operator_pannel.DTO.operatorconfig.OperatorDTO;
import net.vuega.vuega_backend.Operator_pannel.Service.cache.ControlPanelCacheService;

@Service
public class OperatorService {

    private final ControlPanelCacheService cacheService;

    public OperatorService(ControlPanelCacheService cacheService) {
        this.cacheService = cacheService;
    }

    // Validates login credentials against cached OperatorAuth data in Redis.
    public OperatorDTO login(LoginRequest request) {
        Map<String, Object> authData = cacheService.getAuthByEmail(request.getEmail());
        if (authData.isEmpty()) {
            return null;
        }

        String storedPassword = (String) authData.get("password");
        if (storedPassword == null || !BCrypt.checkpw(request.getPassword(), storedPassword)) {
            return null;
        }

        Long operatorId = ((Number) authData.get("operatorId")).longValue();
        Map<String, Object> operatorData = cacheService.getOperatorDetails(operatorId);
        Map<String, Object> licenseData = cacheService.getLicenseByOperator(operatorId);

        String operatorName = operatorData.containsKey("operatorName")
                ? (String) operatorData.get("operatorName")
                : null;
        String companyName = operatorData.containsKey("companyName")
                ? (String) operatorData.get("companyName")
                : null;
        String accountStatus = operatorData.containsKey("status")
                ? (String) operatorData.get("status")
                : null;
        String licenseStatus = licenseData.containsKey("status")
                ? (String) licenseData.get("status")
                : null;

        Integer busLimit = null;
        Integer routeLimit = null;
        if (!licenseData.isEmpty() && licenseData.containsKey("licenseId")) {
            Long licenseId = ((Number) licenseData.get("licenseId")).longValue();
            Map<String, Object> limitsData = cacheService.getLicenseLimitsByLicenseId(licenseId);
            if (!limitsData.isEmpty()) {
                busLimit = limitsData.containsKey("busLimit")
                        ? ((Number) limitsData.get("busLimit")).intValue()
                        : null;
                routeLimit = limitsData.containsKey("routeLimit")
                        ? ((Number) limitsData.get("routeLimit")).intValue()
                        : null;
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
