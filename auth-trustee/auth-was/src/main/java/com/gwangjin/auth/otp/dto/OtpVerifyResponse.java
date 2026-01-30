package com.gwangjin.auth.otp.dto;

public record OtpVerifyResponse(
        boolean success,
        String resultCode,
        String authResultToken
) {}
