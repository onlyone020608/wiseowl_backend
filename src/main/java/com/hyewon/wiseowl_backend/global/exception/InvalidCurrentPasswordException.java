package com.hyewon.wiseowl_backend.global.exception;

import lombok.Getter;

@Getter
public class InvalidCurrentPasswordException extends RuntimeException {
    private final ErrorCode errorCode = ErrorCode.INVALID_CURRENT_PASSWORD;

    public InvalidCurrentPasswordException(String message) {
        super(message);
    }
    public InvalidCurrentPasswordException() {
        super("Current password is incorrect.");
    }
}
