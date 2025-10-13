package com.app.inventory.service;

import com.app.inventory.enums.TokenType;

public interface EmailService {

    void sendEmail(String email, String token, TokenType tokenType);

}
