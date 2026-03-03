package net.vuega.vuega_backend.Service.operatorconfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import net.vuega.vuega_backend.DTO.operatorconfig.LoginRequest;
import net.vuega.vuega_backend.DTO.operatorconfig.OperatorDTO;

@Service
public class OperatorService {

    private final RestClient restClient;

    public OperatorService(@Value("${control-panel.base-url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

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
