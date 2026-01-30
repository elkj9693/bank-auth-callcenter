package com.gwangjin.issuerwas.api.controller;

import com.gwangjin.issuerwas.common.security.RsaKeyProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/issuer/auth")
@RequiredArgsConstructor
public class IssuerKeyController {

    private final RsaKeyProvider rsaKeyProvider;

    @GetMapping("/public-key")
    public Map<String, String> getPublicKey() {
        return Map.of(
                "publicKey", rsaKeyProvider.getPublicKeyAsString(),
                "kid", rsaKeyProvider.getKid());
    }
}
