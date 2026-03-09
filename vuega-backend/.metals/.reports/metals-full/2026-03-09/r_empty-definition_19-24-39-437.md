error id: file://<WORKSPACE>/src/main/java/net/vuega/vuega_backend/Operator_pannel/Service/cache/ControlPanelCacheService.java:net/vuega/vuega_backend/Control_pannel/model/buses/Buses#
file://<WORKSPACE>/src/main/java/net/vuega/vuega_backend/Operator_pannel/Service/cache/ControlPanelCacheService.java
empty definition using pc, found symbol in pc: net/vuega/vuega_backend/Control_pannel/model/buses/Buses#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 400
uri: file://<WORKSPACE>/src/main/java/net/vuega/vuega_backend/Operator_pannel/Service/cache/ControlPanelCacheService.java
text:
```scala
package net.vuega.vuega_backend.Operator_pannel.Service.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.vuega.vuega_backend.Control_pannel.model.buses.@@Buses;
import net.vuega.vuega_backend.Control_pannel.model.licenselimits.LicenseLimits;
import net.vuega.vuega_backend.Control_pannel.model.licenses.Licenses;
import net.vuega.vuega_backend.Control_pannel.model.operatorauth.OperatorAuth;
import net.vuega.vuega_backend.Control_pannel.model.operators.Operators;
import net.vuega.vuega_backend.Control_pannel.model.routes.Route;
import net.vuega.vuega_backend.Control_pannel.repository.buses.BusesRepository;
import net.vuega.vuega_backend.Control_pannel.repository.licenselimits.LicenseLimitsRepository;
import net.vuega.vuega_backend.Control_pannel.repository.licenses.LicenseRepository;
import net.vuega.vuega_backend.Control_pannel.repository.operatorauth.OperatorAuthRepository;
import net.vuega.vuega_backend.Control_pannel.repository.operators.OperatorRepository;
import net.vuega.vuega_backend.Control_pannel.repository.routes.RouteRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ControlPanelCacheService {

    private static final String BUS_KEY_PREFIX = "cp:bus:";
    private static final String ROUTE_KEY_PREFIX = "cp:route:";
    private static final String OPERATOR_KEY_PREFIX = "cp:operator:";
    private static final String AUTH_EMAIL_KEY_PREFIX = "cp:auth:email:";
    private static final String LICENSE_KEY_PREFIX = "cp:license:operator:";
    private static final String LICENSE_LIMITS_KEY_PREFIX = "cp:license_limits:license:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final BusesRepository busesRepository;
    private final RouteRepository routeRepository;
    private final OperatorRepository operatorRepository;
    private final OperatorAuthRepository operatorAuthRepository;
    private final LicenseRepository licenseRepository;
    private final LicenseLimitsRepository licenseLimitsRepository;

    // ======================== REFRESH ALL ========================

    public void refreshAll() {
        log.info("Refreshing all Control Panel data into Redis cache...");
        refreshBuses();
        refreshRoutes();
        refreshOperators();
        refreshAuth();
        refreshLicenses();
        refreshLicenseLimits();
        log.info("Redis cache refresh complete.");
    }

    // ======================== BUSES ========================

    public void refreshBuses() {
        List<Buses> allBuses = busesRepository.findAll();
        for (Buses bus : allBuses) {
            Map<String, Object> busMap = new HashMap<>();
            busMap.put("busId", bus.getBusId());
            busMap.put("operatorId", bus.getOperatorId());
            busMap.put("busNumber", bus.getBusNumber());
            busMap.put("busType", bus.getBusType().name());
            busMap.put("layoutId", bus.getLayoutId());
            busMap.put("status", bus.getStatus().name());
            redisTemplate.opsForHash().putAll(BUS_KEY_PREFIX + bus.getBusId(), busMap);
        }
        log.info("Cached {} buses in Redis", allBuses.size());
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getBusDetails(Long busId) {
        String key = BUS_KEY_PREFIX + busId;
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        if (entries.isEmpty()) {
            // Cache miss - try loading from DB
            Optional<Buses> busOpt = busesRepository.findById(busId);
            if (busOpt.isPresent()) {
                Buses bus = busOpt.get();
                Map<String, Object> busMap = new HashMap<>();
                busMap.put("busId", bus.getBusId());
                busMap.put("operatorId", bus.getOperatorId());
                busMap.put("busNumber", bus.getBusNumber());
                busMap.put("busType", bus.getBusType().name());
                busMap.put("layoutId", bus.getLayoutId());
                busMap.put("status", bus.getStatus().name());
                redisTemplate.opsForHash().putAll(key, busMap);
                return busMap;
            }
            return Map.of("error", "Bus not found", "busId", busId);
        }
        Map<String, Object> result = new HashMap<>();
        entries.forEach((k, v) -> result.put(k.toString(), v));
        return result;
    }

    // ======================== ROUTES ========================

    public void refreshRoutes() {
        List<Route> allRoutes = routeRepository.findAll();
        for (Route route : allRoutes) {
            Map<String, Object> routeMap = new HashMap<>();
            routeMap.put("routeId", route.getRouteId());
            routeMap.put("operatorId", route.getOperatorId());
            routeMap.put("fromCityId", route.getFromCityId());
            routeMap.put("toCityId", route.getToCityId());
            routeMap.put("totalDistance", route.getTotalDistance());
            routeMap.put("status", route.getStatus().name());
            redisTemplate.opsForHash().putAll(ROUTE_KEY_PREFIX + route.getRouteId(), routeMap);
        }
        log.info("Cached {} routes in Redis", allRoutes.size());
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getRouteDetails(Long routeId) {
        String key = ROUTE_KEY_PREFIX + routeId;
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        if (entries.isEmpty()) {
            // Cache miss - try loading from DB
            Optional<Route> routeOpt = routeRepository.findById(routeId);
            if (routeOpt.isPresent()) {
                Route route = routeOpt.get();
                Map<String, Object> routeMap = new HashMap<>();
                routeMap.put("routeId", route.getRouteId());
                routeMap.put("operatorId", route.getOperatorId());
                routeMap.put("fromCityId", route.getFromCityId());
                routeMap.put("toCityId", route.getToCityId());
                routeMap.put("totalDistance", route.getTotalDistance());
                routeMap.put("status", route.getStatus().name());
                redisTemplate.opsForHash().putAll(key, routeMap);
                return routeMap;
            }
            return Map.of("error", "Route not found", "routeId", routeId);
        }
        Map<String, Object> result = new HashMap<>();
        entries.forEach((k, v) -> result.put(k.toString(), v));
        return result;
    }

    // ======================== OPERATORS ========================

    public void refreshOperators() {
        List<Operators> allOperators = operatorRepository.findAll();
        for (Operators op : allOperators) {
            Map<String, Object> opMap = new HashMap<>();
            opMap.put("operatorId", op.getOperatorId());
            opMap.put("operatorName", op.getOperatorName());
            opMap.put("companyName", op.getCompanyName());
            opMap.put("status", op.getStatus().name());
            redisTemplate.opsForHash().putAll(OPERATOR_KEY_PREFIX + op.getOperatorId(), opMap);
        }
        log.info("Cached {} operators in Redis", allOperators.size());
    }

    public Map<String, Object> getOperatorDetails(Long operatorId) {
        String key = OPERATOR_KEY_PREFIX + operatorId;
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        if (entries.isEmpty()) {
            Optional<Operators> opOpt = operatorRepository.findById(operatorId);
            if (opOpt.isPresent()) {
                Operators op = opOpt.get();
                Map<String, Object> opMap = new HashMap<>();
                opMap.put("operatorId", op.getOperatorId());
                opMap.put("operatorName", op.getOperatorName());
                opMap.put("companyName", op.getCompanyName());
                opMap.put("status", op.getStatus().name());
                redisTemplate.opsForHash().putAll(key, opMap);
                return opMap;
            }
            return Map.of("error", "Operator not found", "operatorId", operatorId);
        }
        Map<String, Object> result = new HashMap<>();
        entries.forEach((k, v) -> result.put(k.toString(), v));
        return result;
    }

    // ======================== AUTH ========================

    public void refreshAuth() {
        List<OperatorAuth> allAuth = operatorAuthRepository.findAll();
        for (OperatorAuth auth : allAuth) {
            Map<String, Object> authMap = new HashMap<>();
            authMap.put("operatorId", auth.getOperatorId());
            authMap.put("licenceId", auth.getLicenceId());
            authMap.put("name", auth.getName());
            authMap.put("email", auth.getEmail());
            authMap.put("password", auth.getPassword());
            authMap.put("role", auth.getRole().name());
            redisTemplate.opsForHash().putAll(AUTH_EMAIL_KEY_PREFIX + auth.getEmail(), authMap);
        }
        log.info("Cached {} operator auth entries in Redis", allAuth.size());
    }

    public Map<String, Object> getAuthByEmail(String email) {
        String key = AUTH_EMAIL_KEY_PREFIX + email;
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        if (entries.isEmpty()) {
            Optional<OperatorAuth> authOpt = operatorAuthRepository.findByEmail(email);
            if (authOpt.isPresent()) {
                OperatorAuth auth = authOpt.get();
                Map<String, Object> authMap = new HashMap<>();
                authMap.put("operatorId", auth.getOperatorId());
                authMap.put("licenceId", auth.getLicenceId());
                authMap.put("name", auth.getName());
                authMap.put("email", auth.getEmail());
                authMap.put("password", auth.getPassword());
                authMap.put("role", auth.getRole().name());
                redisTemplate.opsForHash().putAll(key, authMap);
                return authMap;
            }
            return Map.of();
        }
        Map<String, Object> result = new HashMap<>();
        entries.forEach((k, v) -> result.put(k.toString(), v));
        return result;
    }

    // ======================== LICENSES ========================

    public void refreshLicenses() {
        List<Licenses> allLicenses = licenseRepository.findAll();
        for (Licenses lic : allLicenses) {
            Map<String, Object> licMap = new HashMap<>();
            licMap.put("licenseId", lic.getLicenseId());
            licMap.put("operatorId", lic.getOperatorId());
            licMap.put("licenseKey", lic.getLicenseKey());
            licMap.put("startDate", lic.getStartDate() != null ? lic.getStartDate().getTime() : null);
            licMap.put("endDate", lic.getEndDate() != null ? lic.getEndDate().getTime() : null);
            licMap.put("status", lic.getStatus().name());
            redisTemplate.opsForHash().putAll(LICENSE_KEY_PREFIX + lic.getOperatorId(), licMap);
        }
        log.info("Cached {} licenses in Redis", allLicenses.size());
    }

    public Map<String, Object> getLicenseByOperator(Long operatorId) {
        String key = LICENSE_KEY_PREFIX + operatorId;
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        if (entries.isEmpty()) {
            return Map.of();
        }
        Map<String, Object> result = new HashMap<>();
        entries.forEach((k, v) -> result.put(k.toString(), v));
        return result;
    }

    // ======================== LICENSE LIMITS ========================

    public void refreshLicenseLimits() {
        List<Licenses> allLicenses = licenseRepository.findAll();
        for (Licenses lic : allLicenses) {
            LicenseLimits limits = licenseLimitsRepository.findByLicenseId(lic.getLicenseId());
            if (limits != null) {
                Map<String, Object> limitsMap = new HashMap<>();
                limitsMap.put("limitId", limits.getLimitId());
                limitsMap.put("licenseId", limits.getLicenseId());
                limitsMap.put("busLimit", limits.getBusLimit());
                limitsMap.put("routeLimit", limits.getRouteLimit());
                limitsMap.put("patnerLimit", limits.getPatnerLimit());
                limitsMap.put("tripLimit", limits.getTripLimit());
                redisTemplate.opsForHash().putAll(LICENSE_LIMITS_KEY_PREFIX + lic.getLicenseId(), limitsMap);
            }
        }
        log.info("Cached license limits in Redis");
    }

    public Map<String, Object> getLicenseLimitsByLicenseId(Long licenseId) {
        String key = LICENSE_LIMITS_KEY_PREFIX + licenseId;
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        if (entries.isEmpty()) {
            return Map.of();
        }
        Map<String, Object> result = new HashMap<>();
        entries.forEach((k, v) -> result.put(k.toString(), v));
        return result;
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: net/vuega/vuega_backend/Control_pannel/model/buses/Buses#