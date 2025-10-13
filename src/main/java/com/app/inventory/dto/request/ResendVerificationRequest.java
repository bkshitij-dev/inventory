package com.app.inventory.dto.request;

import lombok.Getter;

@Getter
public class ResendVerificationRequest {

    private String token;

    private String email;

}
