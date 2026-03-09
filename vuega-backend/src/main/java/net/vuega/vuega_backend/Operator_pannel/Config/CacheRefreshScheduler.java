package net.vuega.vuega_backend.Operator_pannel.Config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.vuega.vuega_backend.Operator_pannel.Service.cache.ControlPanelCacheService;

@Component
@RequiredArgsConstructor
@Slf4j
public class CacheRefreshScheduler {

    private final ControlPanelCacheService cacheService;

    // Load all Control Panel data into Redis on application startup.
    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        log.info("Application started — loading Control Panel data into Redis...");
        cacheService.refreshAll();
    }

    // Periodically refresh Redis cache from Control Panel tables.
    @Scheduled(fixedRateString = "${cache.refresh.interval:300000}")
    public void scheduledRefresh() {
        log.info("Scheduled cache refresh triggered");
        cacheService.refreshAll();
    }
}
