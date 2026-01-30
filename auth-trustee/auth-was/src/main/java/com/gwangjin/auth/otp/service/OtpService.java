package com.gwangjin.auth.otp.service;

import com.gwangjin.auth.authresult.domain.AuthResultToken;
import com.gwangjin.auth.authresult.domain.AuthResultTokenStatus;
import com.gwangjin.auth.authresult.repository.AuthResultTokenRepository;
import com.gwangjin.auth.identity.domain.TokenStatus;
import com.gwangjin.auth.identity.domain.VerificationToken;
import com.gwangjin.auth.identity.repository.VerificationTokenRepository;
import com.gwangjin.auth.otp.domain.OtpStatus;
import com.gwangjin.auth.otp.domain.OtpTransaction;
import com.gwangjin.auth.otp.dto.OtpRequestResponse;
import com.gwangjin.auth.otp.dto.OtpVerifyResponse;
import com.gwangjin.auth.otp.repository.OtpTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OtpService {

    private static final int OTP_LENGTH = 6;
    private static final int EXPIRE_MINUTES = 3; // OTP 만료(분)
    private static final int MAX_ATTEMPTS = 5;

    private static final int RESULT_TOKEN_EXPIRE_MINUTES = 5; // 최종 결과 토큰 만료(분)

    private final OtpTransactionRepository otpRepo;
    private final VerificationTokenRepository tokenRepository;
    private final AuthResultTokenRepository authResultTokenRepository;

    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.otp.revealInResponse:false}")
    private boolean revealInResponse;

    // 15단계: phoneNumber 대신 verificationToken을 입력으로 받는다
    @Transactional
    public OtpRequestResponse requestOtp(String verificationTokenValue) {

        // 1) 토큰 조회
        VerificationToken token = tokenRepository.findByTokenValue(verificationTokenValue).orElse(null);
        if (token == null) {
            return new OtpRequestResponse(null, null, "***", null);
        }

        // 2) 만료 체크
        if (Instant.now().isAfter(token.getExpiresAt())) {
            token.setStatus(TokenStatus.EXPIRED);
            return new OtpRequestResponse(null, null, "***", null);
        }

        // 3) 재사용 방지(ISSUED만 허용)
        if (token.getStatus() != TokenStatus.ISSUED) {
            return new OtpRequestResponse(null, null, "***", null);
        }

        // 4) 토큰 소비 처리(1회성)
        token.setStatus(TokenStatus.CONSUMED);

        return processOtpRequest(token.getSubscriberId());
    }

    @Transactional
    public OtpRequestResponse requestOtpBySubscriberId(UUID subscriberId) {
        return processOtpRequest(subscriberId);
    }

    private OtpRequestResponse processOtpRequest(UUID subscriberId) {
        // 5) OTP 생성/저장
        String otp = generateOtp();
        Instant expiresAt = Instant.now().plus(EXPIRE_MINUTES, ChronoUnit.MINUTES);

        OtpTransaction tx = OtpTransaction.builder()
                .subscriberId(subscriberId)
                .otpHash(sha256(otp))
                .status(OtpStatus.REQUESTED)
                .attemptCount(0)
                .expiresAt(expiresAt)
                .build();

        tx = otpRepo.save(tx);

        // Forcing reveal for user debugging
        String otpPreview = otp; // revealInResponse ? otp : null;

        System.out
                .println("[OTP REQUESTED] requestId=" + tx.getId() + ", subscriberId=" + subscriberId + ", OTP=" + otp);

        return new OtpRequestResponse(tx.getId().toString(), expiresAt, "SUCCESS", otpPreview);
    }

    // 16단계: OTP 성공 시 authResultToken 발급
    @Transactional
    public OtpVerifyResponse verifyOtp(UUID requestId, String otpCode) {
        Optional<OtpTransaction> opt = otpRepo.findById(requestId);
        if (opt.isEmpty())
            return new OtpVerifyResponse(false, "NOT_FOUND", null);

        OtpTransaction tx = opt.get();

        if (tx.getStatus() == OtpStatus.LOCKED)
            return new OtpVerifyResponse(false, "LOCKED", null);

        if (Instant.now().isAfter(tx.getExpiresAt())) {
            tx.setStatus(OtpStatus.EXPIRED);
            return new OtpVerifyResponse(false, "EXPIRED", null);
        }

        if (tx.getAttemptCount() >= MAX_ATTEMPTS) {
            tx.setStatus(OtpStatus.LOCKED);
            return new OtpVerifyResponse(false, "LOCKED", null);
        }

        boolean match = sha256(otpCode).equals(tx.getOtpHash());
        if (match) {
            tx.setStatus(OtpStatus.VERIFIED);

            // ===== 16단계: 최종 인증 결과 토큰 발급 =====
            String resultTokenValue = generateResultTokenValue();
            Instant resultExpiresAt = Instant.now().plus(RESULT_TOKEN_EXPIRE_MINUTES, ChronoUnit.MINUTES);

            AuthResultToken resultToken = AuthResultToken.builder()
                    .tokenValue(resultTokenValue)
                    .subscriberId(tx.getSubscriberId())
                    .status(AuthResultTokenStatus.ISSUED)
                    .expiresAt(resultExpiresAt)
                    .build();

            authResultTokenRepository.save(resultToken);

            return new OtpVerifyResponse(true, "VERIFIED", resultTokenValue);
        }

        // 실패 처리
        tx.setAttemptCount(tx.getAttemptCount() + 1);
        if (tx.getAttemptCount() >= MAX_ATTEMPTS) {
            tx.setStatus(OtpStatus.LOCKED);
            return new OtpVerifyResponse(false, "LOCKED", null);
        }

        return new OtpVerifyResponse(false, "INVALID_OTP", null);
    }

    private String generateOtp() {
        int bound = (int) Math.pow(10, OTP_LENGTH);
        int num = secureRandom.nextInt(bound);
        return String.format("%0" + OTP_LENGTH + "d", num);
    }

    private String generateResultTokenValue() {
        byte[] buf = new byte[24]; // base64url 약 32자
        secureRandom.nextBytes(buf);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
    }

    private String sha256(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception e) {
            throw new IllegalStateException("hashing failed", e);
        }
    }
}
