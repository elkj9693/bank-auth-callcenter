package com.gwangjin.auth.otp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record OtpVerifyDto(
        @NotBlank(message = "requestId is required")
        String requestId,

        @NotBlank(message = "otpCode is required")
        @Pattern(regexp = "^\\d{6}$", message = "otpCode must be 6 digits")
        String otpCode
) {
    // UUID 변환용 보조 메서드
    public UUID requestIdAsUuid() {
        return UUID.fromString(requestId);
    }
}
