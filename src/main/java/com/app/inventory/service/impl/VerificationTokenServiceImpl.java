package com.app.inventory.service.impl;

import com.app.inventory.model.User;
import com.app.inventory.model.VerificationToken;
import com.app.inventory.repository.VerificationTokenRepository;
import com.app.inventory.service.EmailService;
import com.app.inventory.service.VerificationTokenService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationTokenServiceImpl implements VerificationTokenService {

    private final EmailService emailService;
    private final VerificationTokenRepository verificationTokenRepository;

    @Override
    @Async
    public void createTokenAndSendEmail(User user) {
        invalidateIfExists(user);
        VerificationToken verificationToken = VerificationToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(LocalDateTime.now().plusHours(12))
                .build();
        verificationTokenRepository.save(verificationToken);
        emailService.sendAccountActivationEmail(user.getEmail(), verificationToken.getToken());
    }

    @Override
    public User validateAndGetUser(String token) throws Exception {
        VerificationToken verificationToken = findByToken(token);
        User user = verificationToken.getUser();
        if (user.isActive()) {
            throw new Exception("User account has already been activated");
        }
        if (verificationToken.isExpired()) {
            throw new Exception("Token has expired");
        }
        return user;
    }

    @Override
    public void invalidate(String token) {
        VerificationToken verificationToken = findActiveToken(token);
        verificationToken.setActive(false);
        verificationTokenRepository.save(verificationToken);
    }

    @Override
    public void invalidateAndCreateNewToken(User user) throws Exception {
        if (user.isActive()) {
            throw new Exception("User already verified");
        }
        createTokenAndSendEmail(user);
    }

    @Override
    public void invalidateAndCreateNewToken(String token) throws Exception {
        VerificationToken verificationToken = findByToken(token);
        invalidateAndCreateNewToken(verificationToken.getUser());
    }

    private VerificationToken findActiveToken(String token) {
        return verificationTokenRepository.findActiveToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Token is invalid"));
    }

    private VerificationToken findByToken(String token) {
        return verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Token is invalid"));
    }

    private void invalidateIfExists(User user) {
        Optional<VerificationToken> verificationTokenOpt = verificationTokenRepository.findByUser(user.getId());
        if (verificationTokenOpt.isPresent()) {
            VerificationToken verificationToken = verificationTokenOpt.get();
            verificationToken.setActive(false);
            verificationTokenRepository.save(verificationToken);
        }
    }
}
