package com.gwangjin.auth.otp.dto;

import java.time.Instant;

public record AuthIntegrationRequest(
        String userRef,
        String phone,
        String channel,
        String continueSessionId) {
}
