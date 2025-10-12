package com.app.inventory.exception;

public class VerificationTokenExpiredException extends RuntimeException {

    public VerificationTokenExpiredException(String message) {
        super(message);
    }
}
