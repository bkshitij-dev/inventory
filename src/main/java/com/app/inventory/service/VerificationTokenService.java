package com.app.inventory.service;

import com.app.inventory.model.User;

public interface VerificationTokenService {

    String create(User user);

    User validateAndGetUser(String token) throws Exception;

    void invalidate(String token);

}
