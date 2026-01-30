package com.gwangjin.callcenterwas.infrastructure.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class IssuerClient {

    private final RestClient restClient;

    public IssuerClient(RestClient.Builder builder,
            @Value("${issuer.base-url}") String baseUrl,
            @Value("${issuer.service-token}") String serviceToken) {
        this.restClient = builder
                .baseUrl(baseUrl)
                .defaultHeader("X-Service-Token", serviceToken)
                .build();
    }

    public Map<String, Object> findCandidates(Map<String, String> request) {
        return restClient.post()
                .uri("/issuer/customer/candidates")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public Map<String, Object> requestAuth(Map<String, String> request) {
        return restClient.post()
                .uri("/issuer/auth/request")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public Map<String, Object> verifyAuth(Map<String, String> request) {
        return restClient.post()
                .uri("/issuer/auth/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public Map<String, Object> reportLoss(Map<String, Object> request) {
        return restClient.post()
                .uri("/issuer/card/loss")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public void sendAuditEvent(Map<String, String> eventData) {
        restClient.post()
                .uri("/issuer/audit/events")
                .contentType(MediaType.APPLICATION_JSON)
                .body(eventData)
                .retrieve()
                .toBodilessEntity();
    }

    public Map<String, Object> getCards(String customerRef) {
        return restClient.get()
                .uri("/issuer/customer/{customerRef}/cards", customerRef)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    // ARS Methods
    public Map<String, Object> getPublicKey() {
        return restClient.get()
                .uri("/issuer/auth/public-key")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public Map<String, Object> requestArsIdentify(Map<String, String> request) {
        return restClient.post()
                .uri("/issuer/ars/identify")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public Map<String, Object> requestArsVerify(Map<String, Object> request) {
        return restClient.post()
                .uri("/issuer/ars/verify-pin")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public List<Map<String, Object>> fetchLeads() {
        return restClient.get()
                .uri("/api/v1/leads/eligible")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public void updateLeadResult(String leadId, Map<String, String> result) {
        restClient.post()
                .uri("/api/v1/leads/{leadId}/result", leadId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(result)
                .retrieve()
                .toBodilessEntity();
    }
}
