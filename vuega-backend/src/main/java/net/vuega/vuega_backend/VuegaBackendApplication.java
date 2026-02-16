package net.vuega.vuega_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VuegaBackendApplication {

	public static void main(String[] args) {
		System.out.println("Starting Vuega Backend Application...");
		SpringApplication.run(VuegaBackendApplication.class, args);
	}

}
