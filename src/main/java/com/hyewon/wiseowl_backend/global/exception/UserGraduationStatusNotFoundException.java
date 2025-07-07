package com.hyewon.wiseowl_backend.global.exception;

import lombok.Getter;

@Getter
public class UserGraduationStatusNotFoundException extends RuntimeException {
    private final ErrorCode errorCode = ErrorCode.USER_GRADUATION_STATUS_NOT_FOUND;
    public UserGraduationStatusNotFoundException(String message) {
        super(message);
    }
    public UserGraduationStatusNotFoundException(Long userId) {
        super("No graduation status found for user with ID: " + userId);
    }
    public UserGraduationStatusNotFoundException() {
        super("User's graduation status not found." );
    }

}
