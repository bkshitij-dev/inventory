package com.app.inventory.service;

import com.app.inventory.dto.request.PasswordResetRequest;
import com.app.inventory.dto.request.RegisterRequest;
import com.app.inventory.model.User;

import java.util.Optional;

public interface AuthService {

    void register(RegisterRequest request);

    Optional<User> findByUsernameOrEmail(String username, String email);

    User getByEmail(String email);

    void verify(String token);

    void requestResetPassword(String email);

    void resetPassword(PasswordResetRequest request);

}
