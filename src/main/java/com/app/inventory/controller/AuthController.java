package com.app.inventory.controller;

import com.app.inventory.constant.MessageConstants;
import com.app.inventory.dto.request.*;
import com.app.inventory.dto.response.ApiResponse;
import com.app.inventory.dto.response.AuthResponse;
import com.app.inventory.enums.TokenType;
import com.app.inventory.exception.InvalidTokenException;
import com.app.inventory.model.User;
import com.app.inventory.security.AppUserDetailsService;
import com.app.inventory.security.JwtService;
import com.app.inventory.service.AuthService;
import com.app.inventory.service.CustomMessageSource;
import com.app.inventory.service.ExternalAccessTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    private final ExternalAccessTokenService externalAccessTokenService;
    private final CustomMessageSource customMessageSource;

    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Register as admin in the application")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok(ApiResponse.success(customMessageSource.getMessage(MessageConstants.AUTH_USER_REGISTERED)));
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Login to the system using user email and password")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.usernameOrEmail(), request.password())
        );

        UserDetails userDetails = appUserDetailsService.loadUserByUsername(request.usernameOrEmail());
        String jwtToken = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(ApiResponse.success(customMessageSource.getMessage(MessageConstants.AUTH_USER_LOGGED_IN),
                new AuthResponse(jwtToken)));
    }

    @PostMapping("/verify")
    @Operation(summary = "User verification", description = "Use the token to activate the user")
    public ResponseEntity<ApiResponse<Void>> verify(@Valid @RequestBody TokenRequest request) {
        authService.verify(request.getToken());
        return ResponseEntity.ok(ApiResponse.success(customMessageSource.getMessage(
                MessageConstants.AUTH_USER_ACCOUNT_ACTIVATED)));
    }

    @PostMapping("/resend-verification")
    @Operation(summary = "Resend Verification",
            description = "Resend verification email in case of token expiry or lost token")
    public ResponseEntity<ApiResponse<Void>> resendVerification(@RequestBody ResendVerificationRequest request) {
        if (Objects.isNull(request.getEmail()) && Objects.isNull(request.getToken())) {
            throw new InvalidTokenException(customMessageSource.getMessage(MessageConstants.AUTH_VERIFICATION_LINK_INVALID));
        }
        if (Objects.nonNull(request.getEmail())) {
            User user = authService.getByEmail(request.getEmail());
            externalAccessTokenService.invalidateAndCreateNewToken(user, TokenType.ACCOUNT_VERIFICATION);
        } else {
            externalAccessTokenService.invalidateAndCreateNewToken(request.getToken(), TokenType.ACCOUNT_VERIFICATION);
        }
        return ResponseEntity.ok(ApiResponse.success(customMessageSource.getMessage(
                MessageConstants.AUTH_VERIFICATION_EMAIL_SENT)));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Forgot Password", description = "Send email to reset password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody EmailRequest request) {
        authService.requestResetPassword(request.getEmail());
        return ResponseEntity.ok(ApiResponse.success(customMessageSource.getMessage(
                MessageConstants.AUTH_RESET_PASSWORD_EMAIL_SENT)));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset Password", description = "Update password after requesting for forgotten password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success(customMessageSource.getMessage(MessageConstants.AUTH_PASSWORD_RESET)));
    }
}
