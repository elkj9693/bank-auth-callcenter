// 응답값 지정

package com.gwangjin.auth.otp.dto;

import java.time.Instant;

public record OtpRequestResponse(
        String requestId,
        Instant expiresAt,
        String maskedPhoneNumber,
        String otpPreview
) {}
