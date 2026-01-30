package com.gwangjin.issuerwas.api.controller;

import com.gwangjin.issuerwas.domain.service.IssuerAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/login")
public class LoginController {

    private final IssuerAuthService authService;

    @PostMapping
    public Map<String, Object> login(@RequestBody Map<String, String> req) {
        String name = req.get("username");
        String password = req.get("password");
        return authService.login(name, password);
    }

    @PostMapping("/verify")
    public Map<String, Object> verify(@RequestBody Map<String, String> req) {
        String authResultToken = req.get("authResultToken");
        String customerRef = req.get("customerRef");
        // Old Legacy:
        // String authTxId = req.get("authTxId");
        // String otp = req.get("otp");

        return authService.verifyLoginWithToken(authResultToken, customerRef);
    }
}
