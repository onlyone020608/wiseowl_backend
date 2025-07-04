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
    MAJOR_NOT_FOUND(HttpStatus.NOT_FOUND, "Major not found.");


    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
