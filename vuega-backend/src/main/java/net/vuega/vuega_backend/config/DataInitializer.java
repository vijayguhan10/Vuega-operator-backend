package net.vuega.vuega_backend.config;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.vuega.vuega_backend.Model.Operator;
import net.vuega.vuega_backend.Repository.OperatorRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final OperatorRepository operatorRepository;

    @Override
    public void run(String... args) {
        if (operatorRepository.count() == 0) {
            log.info("Initializing sample operator data...");

            List<Operator> sampleData = List.of(
                Operator.builder()
                    .serviceEmail("admin@vuega.net")
                    .accessToken("tok_abc123")
                    .tokenExpiresAt(LocalDateTime.now().plusDays(30))
                    .operatorName("Vuega Admin")
                    .organizationName("Vuega Transport Ltd")
                    .licenseStatus("ACTIVE")
                    .accountStatus("ACTIVE")
                    .busLimit(50)
                    .routeLimit(20)
                    .lastChecked(LocalDateTime.now())
                    .build(),
                Operator.builder()
                    .serviceEmail("ops@citytransit.com")
                    .accessToken("tok_def456")
                    .tokenExpiresAt(LocalDateTime.now().plusDays(15))
                    .operatorName("City Transit Ops")
                    .organizationName("City Transit Corp")
                    .licenseStatus("ACTIVE")
                    .accountStatus("ACTIVE")
                    .busLimit(100)
                    .routeLimit(40)
                    .lastChecked(LocalDateTime.now().minusHours(6))
                    .build(),
                Operator.builder()
                    .serviceEmail("demo@testbus.org")
                    .accessToken("tok_ghi789")
                    .tokenExpiresAt(LocalDateTime.now().plusDays(7))
                    .operatorName("Demo User")
                    .organizationName("TestBus Inc")
                    .licenseStatus("TRIAL")
                    .accountStatus("ACTIVE")
                    .busLimit(5)
                    .routeLimit(3)
                    .lastChecked(LocalDateTime.now().minusDays(1))
                    .build()
            );

            operatorRepository.saveAll(sampleData);
            log.info("Sample data initialized: {} operators created", sampleData.size());
        } else {
            log.info("Operator data already exists, skipping initialization.");
        }
    }
}
