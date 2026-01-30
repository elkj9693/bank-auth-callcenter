package com.gwangjin.auth.authresult.controller;

import com.gwangjin.auth.authresult.dto.AuthResultConfirmRequest;
import com.gwangjin.auth.authresult.dto.AuthResultConfirmResponse;
import com.gwangjin.auth.authresult.service.AuthResultService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/result")
public class AuthResultController {

    private final AuthResultService authResultService;

    @PostMapping("/confirm")
    public AuthResultConfirmResponse confirm(@Valid @RequestBody AuthResultConfirmRequest req) {
        return authResultService.confirm(req.authResultToken());
    }
}
