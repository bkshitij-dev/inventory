package com.app.inventory.service;

import com.app.inventory.enums.TokenType;
import com.app.inventory.model.User;

public interface ExternalAccessTokenService {

    void createTokenAndSendEmail(User user, TokenType tokenType);

    User validateAndGetUser(String token);

    void invalidate(String token);

    void invalidateAndCreateNewToken(User user, TokenType tokenType);

    void invalidateAndCreateNewToken(String token, TokenType tokenType);

}
