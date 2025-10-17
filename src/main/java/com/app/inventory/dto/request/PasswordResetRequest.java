package com.app.inventory.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PasswordResetRequest {

    @NotBlank(message = "Token must be provided")
    private String token;

    @NotBlank(message = "Password must be provided")
    private String password;
}
