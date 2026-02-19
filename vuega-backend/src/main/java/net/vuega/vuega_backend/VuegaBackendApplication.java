package net.vuega.vuega_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling   // Required for @Scheduled on SeatService.releaseExpiredLocks()
public class VuegaBackendApplication {

	public static void main(String[] args) {
		System.out.println("Starting Vuega Backend Application...");
		SpringApplication.run(VuegaBackendApplication.class, args);
	}

}
