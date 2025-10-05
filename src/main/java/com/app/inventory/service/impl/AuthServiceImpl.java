package com.app.inventory.service.impl;

import com.app.inventory.dto.request.RegisterRequestDto;
import com.app.inventory.enums.Role;
import com.app.inventory.model.User;
import com.app.inventory.repository.UserRepository;
import com.app.inventory.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public void create(RegisterRequestDto request) {
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.STAFF)
                .build();
        userRepository.save(user);
    }
}
