package com.app.inventory.service;

import com.app.inventory.dto.request.RegisterRequest;
import com.app.inventory.model.User;

import java.util.Optional;

public interface AuthService {

    void register(RegisterRequest request) throws Exception;

    Optional<User> findByUsernameOrEmail(String username, String email);

    void verify(String token) throws Exception;

}
