package com.app.inventory.service.impl;

import com.app.inventory.dto.request.RegisterRequest;
import com.app.inventory.enums.Role;
import com.app.inventory.model.User;
import com.app.inventory.repository.UserRepository;
import com.app.inventory.service.AuthService;
import com.app.inventory.service.VerificationTokenService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenService verificationTokenService;

    @Override
    public void register(RegisterRequest request) throws Exception {
        if (findByUsernameOrEmail(request.getUsername(), request.getEmail()).isPresent()) {
            throw new Exception("Username and/or email already exists");
        }
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ADMIN)
                .build();
        userRepository.save(user);
        verificationTokenService.createTokenAndSendEmail(user);

    }

    @Override
    public Optional<User> findByUsernameOrEmail(String username, String email) {
        return userRepository.findByUsernameOrEmail(username, email);
    }

    @Override
    public User getByEmail(String email) {
        return findByUsernameOrEmail(email, email)
                .orElseThrow(() -> new EntityNotFoundException("User doesn't exist"));
    }

    @Override
    public void verify(String token) throws Exception {
        User user = verificationTokenService.validateAndGetUser(token);
        user.setActive(true);
        userRepository.save(user);
        verificationTokenService.invalidate(token);
    }

}
