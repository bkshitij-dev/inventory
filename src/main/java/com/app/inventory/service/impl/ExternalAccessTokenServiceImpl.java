package com.app.inventory.service.impl;

import com.app.inventory.enums.TokenType;
import com.app.inventory.exception.ResourceNotFoundException;
import com.app.inventory.exception.UserAlreadyValidatedException;
import com.app.inventory.exception.VerificationTokenExpiredException;
import com.app.inventory.model.User;
import com.app.inventory.model.ExternalAccessToken;
import com.app.inventory.repository.ExternalAccessTokenRepository;
import com.app.inventory.service.EmailService;
import com.app.inventory.service.ExternalAccessTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExternalAccessTokenServiceImpl implements ExternalAccessTokenService {

    private final EmailService emailService;
    private final ExternalAccessTokenRepository externalAccessTokenRepository;

    @Override
    @Async
    public void createTokenAndSendEmail(User user, TokenType tokenType) {
        invalidateIfExists(user.getId(), tokenType);
        ExternalAccessToken externalAccessToken = ExternalAccessToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(LocalDateTime.now().plusHours(12))
                .tokenType(tokenType)
                .build();
        externalAccessTokenRepository.save(externalAccessToken);
        emailService.sendEmail(user.getEmail(), externalAccessToken.getToken(), tokenType);
    }

    @Override
    public User validateAndGetUser(String token) {
        ExternalAccessToken externalAccessToken = findByToken(token);
        User user = externalAccessToken.getUser();
        if (user.isActive()) {
            throw new UserAlreadyValidatedException("User account has already been activated");
        }
        if (externalAccessToken.isExpired()) {
            throw new VerificationTokenExpiredException("Token has expired");
        }
        return user;
    }

    @Override
    public void invalidate(String token) {
        ExternalAccessToken externalAccessToken = findActiveToken(token);
        externalAccessToken.setActive(false);
        externalAccessTokenRepository.save(externalAccessToken);
    }

    @Override
    public void invalidateAndCreateNewToken(User user, TokenType tokenType) {
        if (tokenType.equals(TokenType.ACCOUNT_VERIFICATION) && user.isActive()) {
            throw new UserAlreadyValidatedException("User account has already been activated");
        }
        createTokenAndSendEmail(user, tokenType);
    }

    @Override
    public void invalidateAndCreateNewToken(String token, TokenType tokenType) {
        ExternalAccessToken externalAccessToken = findByToken(token);
        invalidateAndCreateNewToken(externalAccessToken.getUser(), tokenType);
    }

    private ExternalAccessToken findActiveToken(String token) {
        return externalAccessTokenRepository.findActiveToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token is invalid"));
    }

    private ExternalAccessToken findByToken(String token) {
        return externalAccessTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token is invalid"));
    }

    private void invalidateIfExists(Long userId, TokenType tokenType) {
        Optional<ExternalAccessToken> verificationTokenOpt = externalAccessTokenRepository.findByUserAndTokenType(
                userId, tokenType);
        if (verificationTokenOpt.isPresent()) {
            ExternalAccessToken externalAccessToken = verificationTokenOpt.get();
            externalAccessToken.setActive(false);
            externalAccessTokenRepository.save(externalAccessToken);
        }
    }
}
