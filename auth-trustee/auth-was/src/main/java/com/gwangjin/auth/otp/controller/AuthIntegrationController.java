package com.gwangjin.auth.otp.controller;

import com.gwangjin.auth.common.crypto.Sha256Util;
import com.gwangjin.auth.otp.dto.AuthIntegrationRequest;
import com.gwangjin.auth.otp.dto.AuthIntegrationResponse;
import com.gwangjin.auth.otp.dto.OtpRequestResponse;
import com.gwangjin.auth.otp.service.OtpService;
import com.gwangjin.auth.subscriber.domain.Subscriber;
import com.gwangjin.auth.subscriber.repository.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthIntegrationController {

    private final OtpService otpService;
    private final SubscriberRepository subscriberRepository;

    @PostMapping("/request")
    public ResponseEntity<?> requestAuth(@RequestBody AuthIntegrationRequest req) {
        try {
            if (req.phone() == null) {
                return ResponseEntity.badRequest()
                        .body(new AuthIntegrationResponse(null, null, "Phone number is null", null));
            }

            // 1) Subscriber 조회 (전화번호 해시) - 하이픈 제거 정규화
            String cleanPhone = req.phone().replaceAll("-", "");
            String phoneHash = Sha256Util.sha256(cleanPhone);

            Subscriber subscriber = subscriberRepository.findByPhoneHash(phoneHash)
                    .orElseThrow(() -> new RuntimeException("Subscriber not found for phone: " + cleanPhone));

            // 2) OTP 요청
            OtpRequestResponse otpRes = otpService.requestOtpBySubscriberId(subscriber.getId());

            System.out.println("[AUTH INTEGRATION] userRef=" + req.userRef() + ", channel=" + req.channel());

            return ResponseEntity.ok(new AuthIntegrationResponse(
                    otpRes.requestId(),
                    otpRes.expiresAt(),
                    "OTP 발송 성공",
                    otpRes.otpPreview()));
        } catch (Exception e) {
            e.printStackTrace();
            // Return 500 with explicit error message for debugging
            return ResponseEntity.internalServerError().body(
                    new AuthIntegrationResponse(null, null, "Auth Server Error: " + e.getMessage(), null));
        }
    }
}
