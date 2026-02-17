error id: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Service/operator_config/OperatorService.java:org/springframework/beans/factory/annotation/Value#
file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Service/operator_config/OperatorService.java
empty definition using pc, found symbol in pc: org/springframework/beans/factory/annotation/Value#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 110
uri: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Service/operator_config/OperatorService.java
text:
```scala
package net.vuega.vuega_backend.Service.operator_config;

import org.springframework.beans.factory.annotation.@@Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import net.vuega.vuega_backend.DTO.operator_config.LoginRequest;
import net.vuega.vuega_backend.DTO.operator_config.OperatorDTO;

@Service
public class OperatorService {

    private final RestClient restClient;

    public OperatorService(@Value("${control-panel.base-url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    /**
     * Forwards email + password to the control panel login endpoint
     * and returns the OperatorDTO response.
     */
    public OperatorDTO login(LoginRequest request) {
        try {
            return restClient.post()
                    .uri("/api/operators/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(OperatorDTO.class);
        } catch (HttpClientErrorException e) {
            return null;
        }
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: org/springframework/beans/factory/annotation/Value#