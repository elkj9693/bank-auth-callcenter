package com.gwangjin.issuerwas.domain.service;

import com.gwangjin.issuerwas.domain.entity.AuthTx;
import com.gwangjin.issuerwas.domain.entity.Customer;
import com.gwangjin.issuerwas.domain.repository.AuthTxRepository;
import com.gwangjin.issuerwas.domain.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class IssuerAuthService {

        private final CustomerRepository customerRepository;
        private final RestTemplate restTemplate = new RestTemplate();

        private static final String AUTH_WAS_URL = "http://localhost:8082"; // Hardcoded for demo

        public Map<String, Object> login(String username, String password) {
                try {
                        log.info("Attempting login for user: {}", username);

                        // 1. 1차 인증: ID/PW (여기선 name/password)
                        java.util.List<Customer> allCustomers = customerRepository.findAll();
                        log.info("DEBUG: Found {} customers in DB", allCustomers.size());
                        allCustomers.forEach(c -> log.info("DEBUG: User in DB -> username='{}', name='{}', pw='{}'",
                                        c.getUsername(), c.getName(),
                                        c.getPassword()));

                        Customer customer = allCustomers.stream()
                                        .filter(c -> c.getUsername().equals(username)
                                                        && c.getPassword().equals(password))
                                        .findFirst()
                                        .orElse(null);

                        if (customer == null) {
                                log.warn("Login failed: User not found or invalid password");
                                return Map.of("status", "FAILED", "message", "Invalid username or password");
                        }

                        // 2. Refactored for Popup 2FA: Return PII for Frontend Popup (Step A3-Modified)
                        log.info("Login credentials valid. Returning PII for 2FA Popup.");
                        return Map.of(
                                        "status", "PARTIAL_SESSION",
                                        "customerRef", customer.getCustomerRef(),
                                        "name", customer.getName(),
                                        "birthDate", customer.getBirth(),
                                        "phoneNumber", customer.getPhone());

                } catch (Exception e) {
                        log.error("Unexpected error during login", e);
                        return Map.of("status", "FAILED", "message", "Internal Login Error: " + e.getMessage());
                }
        }

        public Map<String, Object> verifyLoginWithToken(String authResultToken, String customerRef) {
                // 1. Auth Trustee에 Token 검증 요청
                Map<String, String> verifyReq = Map.of("authResultToken", authResultToken);

                try {
                        Map<String, Object> verifyRes = restTemplate.postForObject(
                                        AUTH_WAS_URL + "/api/v1/auth/result/confirm",
                                        verifyReq,
                                        Map.class);

                        if (Boolean.TRUE.equals(verifyRes.get("success"))) {
                                // 2. 세션 승격
                                return Map.of(
                                                "status", "FULL_LOGIN",
                                                "customerRef", customerRef,
                                                "message", "Login successful");
                        } else {
                                return Map.of(
                                                "status", "FAILED",
                                                "message", "Token verification failed: " + verifyRes.get("message"));
                        }
                } catch (Exception e) {
                        log.error("Token verification failed", e);
                        return Map.of("status", "FAILED", "message", "Auth Service Error");
                }
        }

        public Map<String, Object> requestAuth(Map<String, String> request) {
                String url = AUTH_WAS_URL + "/api/v1/auth/request";
                return restTemplate.postForObject(url, request, Map.class);
        }

        public Map<String, Object> verifyAuth(Map<String, String> request) {
                String url = AUTH_WAS_URL + "/api/v1/otp/verify";
                return restTemplate.postForObject(url, request, Map.class);
        }
}
