package com.hyewon.wiseowl_backend.global.exception;

import lombok.Getter;

@Getter
public class UserCompletedCourseNotFoundException extends RuntimeException {
    private final ErrorCode errorCode = ErrorCode.USER_COMPLETED_COURSE_NOT_FOUND;
    public UserCompletedCourseNotFoundException(String message) {
        super(message);
    }
    public UserCompletedCourseNotFoundException(Long userId) {
        super("User completed courses not found with userId: " + userId);
    }
    public UserCompletedCourseNotFoundException() {
        super("User completed courses not found.");
    }
}
