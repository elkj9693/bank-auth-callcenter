package com.gwangjin.auth.authresult.dto;

import java.time.Instant;

public record AuthResultConfirmResponse(
        boolean success,
        String resultCode,
        Instant expiresAt
) {}
