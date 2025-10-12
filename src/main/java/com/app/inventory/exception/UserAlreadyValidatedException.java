package com.app.inventory.exception;

public class UserAlreadyValidatedException extends RuntimeException {

    public UserAlreadyValidatedException(String message) {
        super(message);
    }
}
