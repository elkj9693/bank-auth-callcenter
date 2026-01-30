package com.gwangjin.callcenterwas.infrastructure.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class AuthTrusteeClient {

    private final RestClient restClient;

    public AuthTrusteeClient(RestClient.Builder builder,
            @Value("${auth.base-url:http://localhost:8082}") String baseUrl) {
        this.restClient = builder
                .baseUrl(baseUrl)
                .build();
    }

    public Map<String, Object> requestAuth(Map<String, String> request) {
        return restClient.post()
                .uri("/api/v1/auth/request")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public Map<String, Object> verifyAuth(Map<String, String> request) {
        return restClient.post()
                .uri("/api/v1/otp/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }
}
