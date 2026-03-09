error id: file://<WORKSPACE>/src/main/java/net/vuega/vuega_backend/VuegaBackendApplication.java:_empty_/EnableJpaAuditing#
file://<WORKSPACE>/src/main/java/net/vuega/vuega_backend/VuegaBackendApplication.java
empty definition using pc, found symbol in pc: _empty_/EnableJpaAuditing#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 348
uri: file://<WORKSPACE>/src/main/java/net/vuega/vuega_backend/VuegaBackendApplication.java
text:
```scala
package net.vuega.vuega_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableJpaAu@@diting
public class VuegaBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(VuegaBackendApplication.class, args);
	}

}

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/EnableJpaAuditing#