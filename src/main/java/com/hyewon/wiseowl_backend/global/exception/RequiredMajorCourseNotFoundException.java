package com.hyewon.wiseowl_backend.global.exception;

import lombok.Getter;

@Getter
public class RequiredMajorCourseNotFoundException extends RuntimeException {
    private final ErrorCode errorCode = ErrorCode.REQUIRED_MAJOR_COURSE_NOT_FOUND;

    public RequiredMajorCourseNotFoundException(String message) {
        super(message);
    }
    public RequiredMajorCourseNotFoundException() {
        super("Required major course not found");
    }
    public RequiredMajorCourseNotFoundException(Long requiredMajorCourseId) {
        super("Required major course not found with id: " + requiredMajorCourseId);
    }
}
