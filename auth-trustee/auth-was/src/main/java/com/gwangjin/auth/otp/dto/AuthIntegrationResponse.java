package com.gwangjin.auth.otp.dto;

import java.time.Instant;

public record AuthIntegrationResponse(
                String authTxId,
                Instant expiresAt,
                String message,
                String otpCode) {
}
