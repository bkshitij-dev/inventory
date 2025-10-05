package com.app.inventory.controller;

import com.app.inventory.dto.request.RegisterRequestDto;
import com.app.inventory.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    ResponseEntity<?> create(@Valid @RequestBody RegisterRequestDto request) {
        authService.create(request);
        return ResponseEntity.ok("Created");
    }
}
