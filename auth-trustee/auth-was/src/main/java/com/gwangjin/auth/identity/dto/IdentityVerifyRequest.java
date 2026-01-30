package com.gwangjin.auth.identity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record IdentityVerifyRequest(
                @NotBlank(message = "name is required") String name,

                @NotBlank(message = "birthDate is required") @Pattern(regexp = "^\\d{6}$", message = "birthDate must be YYMMDD") String birthDate,

                @NotBlank(message = "phoneNumber is required") @Pattern(regexp = "^\\d{10,11}$", message = "phoneNumber must be 10~11 digits") String phoneNumber) {
}
