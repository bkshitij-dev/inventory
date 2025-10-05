package com.app.inventory.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class RegisterRequestDto {

    @NotBlank(message = "Username must be provided")
    private String username;

    @NotBlank(message = "Email must be provided")
    private String email;

    @NotBlank(message = "Password must be provided")
    private String password;


}
