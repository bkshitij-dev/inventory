package com.app.inventory.service;

import com.app.inventory.model.User;

public interface VerificationTokenService {

    void createTokenAndSendEmail(User user);

    User validateAndGetUser(String token) throws Exception;

    void invalidate(String token);

    void invalidateAndCreateNewToken(String token) throws Exception;

}
