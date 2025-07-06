package com.hyewon.wiseowl_backend.global.exception;

import lombok.Getter;

@Getter
public class CompletedCourseAlreadyExistsException extends RuntimeException {
  private final ErrorCode errorCode = ErrorCode.COMPLETED_COURSE_ALREADY_EXISTS;
    public CompletedCourseAlreadyExistsException(String message) {
        super(message);
    }
    public CompletedCourseAlreadyExistsException() {
      super("Completed courses already exist. Use update API instead.");
    }
}
