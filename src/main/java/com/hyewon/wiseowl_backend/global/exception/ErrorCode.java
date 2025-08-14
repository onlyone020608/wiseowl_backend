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
    USER_REQUIREMENT_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "User's requirement status not found."),
    USER_MAJOR_NOT_FOUND(HttpStatus.NOT_FOUND, "User's major not found."),
    CREDIT_REQUIREMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Credit requirement not found."),
    REQUIRED_MAJOR_COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "Required major course not found."),
    REQUIRED_LIBERAL_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "Required library category not found."),
    USER_REQUIRED_COURSE_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "User requires courses status not found."),
    USER_COMPLETED_COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "User completed courses not found."),
    INVALID_CURRENT_PASSWORD(HttpStatus.BAD_REQUEST, "Current password is incorrect."),
    FACILITY_NOT_FOUND(HttpStatus.NOT_FOUND, "No facilities are registered in the system."),
    ORGANIZATION_NOT_FOUND(HttpStatus.NOT_FOUND, "Organization not found."),
    SEMESTER_NOT_FOUND(HttpStatus.NOT_FOUND, "Semester not found.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
