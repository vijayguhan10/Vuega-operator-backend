error id: file://<WORKSPACE>/src/main/java/net/vuega/vuega_backend/VuegaBackendApplication.java:_empty_/EnableScheduling#
file://<WORKSPACE>/src/main/java/net/vuega/vuega_backend/VuegaBackendApplication.java
empty definition using pc, found symbol in pc: _empty_/EnableScheduling#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 330
uri: file://<WORKSPACE>/src/main/java/net/vuega/vuega_backend/VuegaBackendApplication.java
text:
```scala
package net.vuega.vuega_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableSched@@uling
@EnableJpaAuditing
public class VuegaBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(VuegaBackendApplication.class, args);
	}

}

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/EnableScheduling#