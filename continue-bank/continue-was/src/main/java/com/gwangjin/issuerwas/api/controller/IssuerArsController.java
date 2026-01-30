package com.gwangjin.issuerwas.api.controller;

import com.gwangjin.issuerwas.domain.service.IssuerArsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/issuer/ars")
@RequiredArgsConstructor
public class IssuerArsController {

    private final IssuerArsService arsService;

    @PostMapping("/identify")
    public Map<String, Object> identify(@RequestBody Map<String, String> request) {
        // Input: { "phoneNumber": "010..." }
        return arsService.identifyCustomer(request.get("phoneNumber"));
    }

    @PostMapping("/verify-pin")
    public Map<String, Object> verifyPin(@RequestBody Map<String, Object> request) {
        // Input: { "customerRef", "kid", "ciphertext", "nonce", ... }
        return arsService.verifyPinAndGetCards(request);
    }
}
