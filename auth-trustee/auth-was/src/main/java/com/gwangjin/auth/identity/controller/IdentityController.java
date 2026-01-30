package com.gwangjin.auth.identity.controller;

import com.gwangjin.auth.identity.dto.IdentityVerifyRequest;
import com.gwangjin.auth.identity.dto.IdentityVerifyResponse;
import com.gwangjin.auth.identity.service.IdentityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/identity")
public class IdentityController {

    private final IdentityService identityService;

    @PostMapping("/verify")
    public IdentityVerifyResponse verify(@Valid @RequestBody IdentityVerifyRequest req) {
        return identityService.verifyIdentity(req.name(), req.birthDate(), req.phoneNumber());
    }
}
