package com.app.inventory.security;

import com.app.inventory.model.User;
import com.app.inventory.repository.UserRepository;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SpringSecurityAuditorAware implements AuditorAware<User> {

    private final UserRepository userRepository;

    public SpringSecurityAuditorAware(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> getCurrentAuditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return userRepository.findById(1L);
        }

        return Optional.of((User) auth.getPrincipal());
    }
}

