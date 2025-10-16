package com.app.inventory.service.impl;

import com.app.inventory.constant.MessageConstants;
import com.app.inventory.dto.request.PasswordResetRequest;
import com.app.inventory.dto.request.RegisterRequest;
import com.app.inventory.enums.Role;
import com.app.inventory.enums.TokenType;
import com.app.inventory.exception.ResourceNotFoundException;
import com.app.inventory.exception.UserAlreadyExistsException;
import com.app.inventory.model.User;
import com.app.inventory.repository.UserRepository;
import com.app.inventory.service.AuthService;
import com.app.inventory.service.CustomMessageSource;
import com.app.inventory.service.ExternalAccessTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ExternalAccessTokenService externalAccessTokenService;
    private final CustomMessageSource customMessageSource;

    @Override
    public void register(RegisterRequest request) {
        if (findByUsernameOrEmail(request.getUsername(), request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException(customMessageSource.getMessage(MessageConstants.AUTH_USER_ALREADY_EXISTS));
        }
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ADMIN)
                .build();
        userRepository.save(user);
        externalAccessTokenService.createTokenAndSendEmail(user, TokenType.ACCOUNT_VERIFICATION);
    }

    @Override
    public Optional<User> findByUsernameOrEmail(String username, String email) {
        return userRepository.findByUsernameOrEmail(username, email);
    }

    @Override
    public User getByEmail(String email) {
        return findByUsernameOrEmail(email, email)
                .orElseThrow(() -> new ResourceNotFoundException(customMessageSource.getMessage(
                        MessageConstants.AUTH_USER_NOT_EXISTS, new Object[] {email})));
    }

    @Override
    public void verify(String token) {
        User user = externalAccessTokenService.validateAndGetUser(token);
        user.setActive(true);
        userRepository.save(user);
        externalAccessTokenService.invalidate(token);
    }

    @Override
    public void requestResetPassword(String email) {
        User user = getByEmail(email);
        externalAccessTokenService.createTokenAndSendEmail(user, TokenType.PASSWORD_RESET);
    }

    @Override
    public void resetPassword(PasswordResetRequest request) {
        User user = getByEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }
}
