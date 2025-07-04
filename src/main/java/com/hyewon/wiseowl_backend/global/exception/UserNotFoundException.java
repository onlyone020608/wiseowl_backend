package com.hyewon.wiseowl_backend.global.exception;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {
    private final ErrorCode errorCode =  ErrorCode.USER_NOT_FOUND;
    public UserNotFoundException(String message) {
        super(message);
    }
    public UserNotFoundException(Long userId) {
        super("User not found with id: " + userId);
    }
    public UserNotFoundException() {
        super("User not found.");
    }
}
