package com.app.inventory.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class EmailRequest {

    @NotBlank(message = "Email is required")
    private String email;

}
