package net.vuega.vuega_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

// Application entry point — scheduling and WebSocket message broker enabled.
@SpringBootApplication
@EnableScheduling
public class VuegaBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(VuegaBackendApplication.class, args);
	}

}
