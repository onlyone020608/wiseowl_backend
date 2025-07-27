package com.hyewon.wiseowl_backend.global.exception;

import lombok.Getter;

@Getter
public class SemesterNotFoundException extends RuntimeException {
    private final ErrorCode errorCode = ErrorCode.SEMESTER_NOT_FOUND;
    public SemesterNotFoundException(String message) {
        super(message);
    }
    public SemesterNotFoundException(Long semesterId) {
        super(
                "Semester not found with id: "+semesterId
        );
    }

    public SemesterNotFoundException() {
        super(
                "Semester not found."
        );
    }
}
