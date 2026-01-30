package com.gwangjin.auth.authresult.service;

import com.gwangjin.auth.authresult.domain.AuthResultToken;
import com.gwangjin.auth.authresult.domain.AuthResultTokenStatus;
import com.gwangjin.auth.authresult.dto.AuthResultConfirmResponse;
import com.gwangjin.auth.authresult.repository.AuthResultTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthResultService {

    private final AuthResultTokenRepository authResultTokenRepository;

    @Transactional
    public AuthResultConfirmResponse confirm(String tokenValue) {

        AuthResultToken token = authResultTokenRepository.findByTokenValue(tokenValue).orElse(null);
        if (token == null) {
            return new AuthResultConfirmResponse(false, "TOKEN_NOT_FOUND", null);
        }

        if (Instant.now().isAfter(token.getExpiresAt())) {
            token.setStatus(AuthResultTokenStatus.EXPIRED);
            return new AuthResultConfirmResponse(false, "TOKEN_EXPIRED", token.getExpiresAt());
        }

        if (token.getStatus() != AuthResultTokenStatus.ISSUED) {
            return new AuthResultConfirmResponse(false, "TOKEN_ALREADY_USED", token.getExpiresAt());
        }

        token.setStatus(AuthResultTokenStatus.CONSUMED);
        return new AuthResultConfirmResponse(true, "CONFIRMED", token.getExpiresAt());
    }
}
