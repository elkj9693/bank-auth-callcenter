package com.gwangjin.auth.otp.dto;

import jakarta.validation.constraints.NotBlank;

public record OtpRequestDto(
        @NotBlank(message = "verificationToken is required")
        String verificationToken
) {}
