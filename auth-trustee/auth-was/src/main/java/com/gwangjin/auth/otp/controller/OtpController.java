package com.gwangjin.auth.otp.controller;

import com.gwangjin.auth.otp.dto.OtpRequestDto;
import com.gwangjin.auth.otp.dto.OtpRequestResponse;
import com.gwangjin.auth.otp.dto.OtpVerifyDto;
import com.gwangjin.auth.otp.dto.OtpVerifyResponse;
import com.gwangjin.auth.otp.service.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/otp")
public class OtpController {

    private final OtpService otpService;

    @PostMapping("/request")
    public OtpRequestResponse request(@Valid @RequestBody OtpRequestDto req) {
        return otpService.requestOtp(req.verificationToken());
    }

    @PostMapping("/verify")
    public OtpVerifyResponse verify(@Valid @RequestBody OtpVerifyDto req) {
        return otpService.verifyOtp(req.requestIdAsUuid(), req.otpCode());
    }
}
