package com.app.inventory.controller;

import com.app.inventory.dto.response.ApiResponse;
import com.app.inventory.dto.response.AuthResponse;
import com.app.inventory.dto.request.LoginRequest;
import com.app.inventory.dto.request.RegisterRequest;
import com.app.inventory.exception.InvalidTokenException;
import com.app.inventory.model.User;
import com.app.inventory.security.AppUserDetailsService;
import com.app.inventory.security.JwtService;
import com.app.inventory.service.AuthService;
import com.app.inventory.service.VerificationTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints to manage user authentication and verification")
public class AuthController {

    private final AppUserDetailsService appUserDetailsService;
    private final AuthenticationManager authenticationManager;
    private final AuthService authService;
    private final JwtService jwtService;
    private final VerificationTokenService verificationTokenService;

    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Register as admin in the application")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully"));
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Login to the system using user email and password")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.usernameOrEmail(),
                        request.password()
                )
        );

        UserDetails userDetails = appUserDetailsService.loadUserByUsername(request.usernameOrEmail());
        String jwtToken = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(ApiResponse.success("User logged in successfully",
                new AuthResponse(jwtToken)));
    }

    @GetMapping("/verify")
    @Operation(summary = "User verification", description = "Use the token to activate the user")
    public ResponseEntity<ApiResponse<Void>> verify(@RequestParam("token") String token) {
        authService.verify(token);
        return ResponseEntity.ok(ApiResponse.success("User account activated successfully"));
    }

    @GetMapping("/resend-verification")
    @Operation(summary = "Resend Verification",
            description = "Resend verification email in case of token expiry or lost token")
    public ResponseEntity<ApiResponse<Void>> resendVerification(
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "token", required = false) String token) {
        if (Objects.isNull(email) && Objects.isNull(token)) {
            throw new InvalidTokenException("Invalid Verification Link");
        }
        if (Objects.nonNull(email)) {
            User user = authService.getByEmail(email);
            verificationTokenService.invalidateAndCreateNewToken(user);
        } else {
            verificationTokenService.invalidateAndCreateNewToken(token);
        }
        return ResponseEntity.ok(ApiResponse.success("Account verification email sent"));
    }
}
