package net.vuega.vuega_backend.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.vuega.vuega_backend.Model.operator_config.OperatorConfig;
import net.vuega.vuega_backend.Repository.OperatorConfigRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final OperatorConfigRepository operatorConfigRepository;

    @Override
    public void run(String... args) {
        if (operatorConfigRepository.count() == 0) {
            log.info("Initializing sample operator_config data...");

            List<OperatorConfig> sampleData = List.of(
                OperatorConfig.builder()
                    .licenseKey("VUEGA-PRO-2026-ABCD-1234")
                    .lastChecked(LocalDateTime.now().minusDays(1))
                    .build(),
                OperatorConfig.builder()
                    .licenseKey("VUEGA-ENT-2026-EFGH-5678")
                    .lastChecked(LocalDateTime.now().minusHours(6))
                    .build(),
                OperatorConfig.builder()
                    .licenseKey("VUEGA-STD-2026-IJKL-9012")
                    .lastChecked(LocalDateTime.now())
                    .build()
            );

            operatorConfigRepository.saveAll(sampleData);
            log.info("Sample data initialized: {} operator configs created", sampleData.size());
        } else {
            log.info("Operator config data already exists, skipping initialization.");
        }
    }
}
