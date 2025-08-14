package com.hyewon.wiseowl_backend.global.exception;

import lombok.Getter;

@Getter
public class UserRequiredCourseStatusNotFoundException extends RuntimeException {
    private final ErrorCode errorCode = ErrorCode.USER_REQUIRED_COURSE_STATUS_NOT_FOUND;

    public UserRequiredCourseStatusNotFoundException(String message) {
        super(message);
    }
    public UserRequiredCourseStatusNotFoundException() {
        super("User requires courses status not found.");
    }
    public UserRequiredCourseStatusNotFoundException(Long userId) {
        super("User requires courses status not found with userId: " + userId);
    }
}
