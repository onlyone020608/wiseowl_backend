package com.hyewon.wiseowl_backend.global.exception;

import lombok.Getter;

@Getter
public class UserMajorNotFoundException extends RuntimeException {
    private final ErrorCode errorCode = ErrorCode.USER_MAJOR_NOT_FOUND;

    public UserMajorNotFoundException(String message) {
        super(message);
    }
    public UserMajorNotFoundException() {
        super("User's major not found.");
    }
    public UserMajorNotFoundException(Long userId) {
        super("User's major not found with user id: " + userId);
    }
}
