package com.app.inventory.service.impl;

import com.app.inventory.model.User;
import com.app.inventory.model.VerificationToken;
import com.app.inventory.repository.VerificationTokenRepository;
import com.app.inventory.service.VerificationTokenService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationTokenServiceImpl implements VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;

    @Override
    public String create(User user) {
        invalidateIfExists(user);
        VerificationToken verificationToken = VerificationToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(LocalDateTime.now().plusHours(12))
                .build();
        verificationTokenRepository.save(verificationToken);
        return verificationToken.getToken();
    }

    @Override
    public User validateAndGetUser(String token) throws Exception {
        VerificationToken verificationToken = findByToken(token);
        if (verificationToken.isExpired()) {
            throw new Exception("Token has expired");
        }
        return verificationToken.getUser();
    }

    @Override
    public void invalidate(String token) {
        VerificationToken verificationToken = findByToken(token);
        verificationToken.setActive(false);
        verificationTokenRepository.save(verificationToken);
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
