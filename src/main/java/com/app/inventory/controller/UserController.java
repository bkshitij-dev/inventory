package com.app.inventory.controller;

import com.app.inventory.dto.request.UserRequestDto;
import com.app.inventory.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    ResponseEntity<?> create(@Valid @RequestBody UserRequestDto request) {
        userService.create(request);
        return ResponseEntity.ok("Created");
    }
}
