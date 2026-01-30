package com.gwangjin.auth.identity.dto;

import java.time.Instant;

public record IdentityVerifyResponse(
        boolean verified,
        String resultCode,
        String verificationToken,
        Instant tokenExpiresAt,
        String maskedPhoneNumber
) {}
