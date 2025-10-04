package com.app.inventory.service.impl;

import com.app.inventory.dto.request.UserRequestDto;
import com.app.inventory.enums.Role;
import com.app.inventory.model.User;
import com.app.inventory.repository.UserRepository;
import com.app.inventory.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public void create(UserRequestDto request) {
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .role(Role.STAFF)
                .build();
        userRepository.save(user);
    }
}
