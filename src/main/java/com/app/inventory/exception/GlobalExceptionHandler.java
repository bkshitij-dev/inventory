package com.app.inventory.exception;

import com.app.inventory.constant.ErrorCode;
import com.app.inventory.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        ApiResponse<Void> response = ApiResponse.error("Something went wrong", ErrorCode.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    ResponseEntity<ApiResponse<Void>> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage(), ErrorCode.USER_ALREADY_EXISTS);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(UserAlreadyValidatedException.class)
    ResponseEntity<ApiResponse<Void>> handleUserAlreadyValidated(UserAlreadyValidatedException ex) {
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage(), ErrorCode.USER_ALREADY_VALIDATED);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(VerificationTokenExpiredException.class)
    ResponseEntity<ApiResponse<Void>> handleVerificationTokenExpired(VerificationTokenExpiredException ex) {
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage(), ErrorCode.VERIFICATION_TOKEN_EXPIRED);
        return ResponseEntity.status(HttpStatus.GONE).body(response);
    }

    @ExceptionHandler(InvalidTokenException.class)
    ResponseEntity<ApiResponse<Void>> handleInvalidToken(InvalidTokenException ex) {
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage(), ErrorCode.INVALID_TOKEN);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<List<String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .toList();

        ApiResponse<List<String>> response = new ApiResponse<>(false, "Validation failed.", errors,
                ErrorCode.VALIDATION_ERROR);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage(), ErrorCode.RESOURCE_NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}

