package com.hyewon.wiseowl_backend.global.exception;

import lombok.Getter;

@Getter
public class UserRequirementStatusNotFoundException extends RuntimeException {
    private final ErrorCode errorCode = ErrorCode.USER_REQUIREMENT_STATUS_NOT_FOUND;

    public UserRequirementStatusNotFoundException(String message) {
        super(message);
    }
    public UserRequirementStatusNotFoundException(Long userId) {
        super("No requirement status found for user with ID: " + userId);
    }
    public UserRequirementStatusNotFoundException() {
        super("User's requirement status not found." );
    }
}
