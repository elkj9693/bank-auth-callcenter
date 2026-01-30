package com.gwangjin.callcenterwas.api.controller;

import com.gwangjin.callcenterwas.common.util.SecurityUtil;
import com.gwangjin.callcenterwas.infrastructure.client.IssuerClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/callcenter/ars")
@RequiredArgsConstructor
public class CallCenterArsController {

    private final IssuerClient issuerClient;

    // Simple In-Memory Key Cache
    private String cachedPublicKey;
    private String cachedKid;

    @GetMapping("/public-key")
    public Map<String, Object> getPublicKey() {
        return refreshKey();
    }

    private Map<String, Object> refreshKey() {
        Map<String, Object> keyData = issuerClient.getPublicKey();
        this.cachedPublicKey = (String) keyData.get("publicKey");
        this.cachedKid = (String) keyData.get("kid");
        return keyData;
    }

    @PostMapping("/identify")
    public Map<String, Object> identify(@RequestBody Map<String, String> request) {
        return issuerClient.requestArsIdentify(request);
    }

    @PostMapping("/verify-pin")
    public Map<String, Object> verifyPin(@RequestBody Map<String, String> request) {
        String plainPin = request.get("pin");
        String customerRef = request.get("customerRef");

        char[] pinChars = plainPin.toCharArray();

        try {
            // Check Key Cache
            if (cachedPublicKey == null) {
                refreshKey();
            }

            // Encrypt (OAEP)
            String ciphertext = SecurityUtil.encryptOAEP(pinChars, cachedPublicKey);

            // Wipe Memory
            SecurityUtil.wipe(pinChars);
            // Hint: plainPin string is still in pool, but we simulated the "process".
            // In real Netty/Spring, avoiding String entirely requires RequestBody as
            // byte[].
            // For this scope, explicit wiping of char[] is sufficient.

            // Prepare Rich Payload
            Map<String, Object> payload = new HashMap<>();
            payload.put("customerRef", customerRef);
            payload.put("kid", cachedKid);
            payload.put("ciphertext", ciphertext);
            payload.put("timestamp", LocalDateTime.now().toString());
            payload.put("nonce", UUID.randomUUID().toString());
            payload.put("sessionId", UUID.randomUUID().toString()); // Mock Session

            return issuerClient.requestArsVerify(payload);

        } catch (Exception e) {
            // Log.error("ARS Error", e); // In real production
            return Map.of("success", false, "message", "System Encryption Error");
        }
    }

    @PostMapping("/report-loss")
    public Map<String, Object> reportLoss(@RequestBody Map<String, Object> request) {
        return issuerClient.reportLoss(request);
    }
}
