package com.hyewon.wiseowl_backend.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "Course not found."),
    LIBERAL_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "Library category not found."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found."),
    PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, "Profile not found."),
    MAJOR_NOT_FOUND(HttpStatus.NOT_FOUND, "Major not found."),
    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "Email already registered."),
    COMPLETED_COURSE_ALREADY_EXISTS(HttpStatus.CONFLICT, "Completed courses already exist."),
    COURSE_OFFERING_NOT_FOUND(HttpStatus.NOT_FOUND, "Course offering not found."),
    USER_GRADUATION_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "User's graduation status not found."),
    USER_MAJOR_NOT_FOUND(HttpStatus.NOT_FOUND, "User's major not found."),
    CREDIT_REQUIREMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Credit requirement not found.");



    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
