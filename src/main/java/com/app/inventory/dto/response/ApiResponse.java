package com.app.inventory.dto.response;

import lombok.Getter;

import java.time.Instant;

@Getter
public class ApiResponse<T> {

    private boolean success;

    private String message;

    private T data;

    private String errorCode;

    private Instant timestamp;

    public ApiResponse(boolean success, String message, T data, String errorCode) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.errorCode = errorCode;
        this.timestamp = Instant.now();
    }

    public static <T> ApiResponse<T> success(String message) {
        return success(message, null);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null);
    }

    public static <T> ApiResponse<T> error(String message, String errorCode) {
        return new ApiResponse<>(false, message, null, errorCode);
    }
}
