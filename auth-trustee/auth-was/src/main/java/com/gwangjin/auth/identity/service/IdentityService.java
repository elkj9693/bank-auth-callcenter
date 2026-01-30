package com.gwangjin.auth.identity.service;

import com.gwangjin.auth.common.crypto.Sha256Util;
import com.gwangjin.auth.identity.domain.TokenStatus;
import com.gwangjin.auth.identity.domain.VerificationToken;
import com.gwangjin.auth.identity.dto.IdentityVerifyResponse;
import com.gwangjin.auth.identity.repository.VerificationTokenRepository;
import com.gwangjin.auth.subscriber.domain.Subscriber;
import com.gwangjin.auth.subscriber.domain.SubscriberStatus;
import com.gwangjin.auth.subscriber.repository.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class IdentityService {

    private static final int TOKEN_EXPIRE_MINUTES = 5;

    private final SubscriberRepository subscriberRepository;
    private final VerificationTokenRepository tokenRepository;

    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public IdentityVerifyResponse verifyIdentity(String name, String birthDateYYYYMMDD, String phoneNumber) {

        // 입력 정규화(실무형): 공백/하이픈 등으로 인한 MISMATCH 방지
        String normPhone = phoneNumber == null ? "" : phoneNumber.replaceAll("\\D", ""); // 숫자만
        String normName = name == null ? "" : name.trim();
        String normBirth = birthDateYYYYMMDD == null ? "" : birthDateYYYYMMDD.trim();

        String phoneHash = Sha256Util.sha256(normPhone);
        Subscriber s = subscriberRepository.findByPhoneHash(phoneHash).orElse(null);

        if (s == null) {
            return new IdentityVerifyResponse(false, "NOT_FOUND", null, null, maskPhone(normPhone));
        }

        // 상태 체크
        if (s.getStatus() == SubscriberStatus.SUSPENDED) {
            return new IdentityVerifyResponse(false, "SUSPENDED", null, null, maskPhone(normPhone));
        }
        if (s.getStatus() == SubscriberStatus.WITHDRAWN) {
            return new IdentityVerifyResponse(false, "WITHDRAWN", null, null, maskPhone(normPhone));
        }

        // name/birthDate 해시 비교
        boolean nameMatch = Sha256Util.sha256(normName).equals(s.getNameHash());
        boolean birthMatch = Sha256Util.sha256(normBirth).equals(s.getBirthDateHash());

        if (!nameMatch || !birthMatch) {
            return new IdentityVerifyResponse(false, "MISMATCH", null, null, maskPhone(normPhone));
        }

        // 토큰 발급
        String tokenValue = generateTokenValue();
        Instant expiresAt = Instant.now().plus(TOKEN_EXPIRE_MINUTES, ChronoUnit.MINUTES);

        VerificationToken token = VerificationToken.builder()
                .tokenValue(tokenValue)
                .subscriberId(s.getId())
                .status(TokenStatus.ISSUED)
                .expiresAt(expiresAt)
                .build();

        tokenRepository.save(token);

        return new IdentityVerifyResponse(true, "VERIFIED", tokenValue, expiresAt, maskPhone(normPhone));
    }

    private String generateTokenValue() {
        byte[] buf = new byte[24]; // 24 bytes -> base64url 약 32자 내외
        secureRandom.nextBytes(buf);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) return "***";
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
}
