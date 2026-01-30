package com.gwangjin.auth.authresult.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthResultConfirmRequest(
        @NotBlank(message = "authResultToken is required")
        String authResultToken
) {}
