package com.app.inventory.bootstrap;

import com.app.inventory.enums.Role;
import com.app.inventory.model.User;
import com.app.inventory.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.findById(0L).isPresent()) {
            return;
        }

        User systemUser = User.builder()
                .username("system")
                .email("system@app.com")
                .password(passwordEncoder.encode("System@123"))
                .role(Role.SYSTEM)
                .active(true)
                .build();
        systemUser.setCreatedBy(systemUser);
        systemUser.setLastModifiedBy(systemUser);
        userRepository.save(systemUser);
    }
}

