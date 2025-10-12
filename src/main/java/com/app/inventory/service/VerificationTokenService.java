package com.app.inventory.service;

import com.app.inventory.model.User;

public interface VerificationTokenService {

    void createTokenAndSendEmail(User user);

    User validateAndGetUser(String token);

    void invalidate(String token);

    void invalidateAndCreateNewToken(User user);

    void invalidateAndCreateNewToken(String token);

}
