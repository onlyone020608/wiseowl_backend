package com.hyewon.wiseowl_backend.global.exception;

import lombok.Getter;

@Getter
public class CourseNotFoundException extends RuntimeException {
  private final ErrorCode errorCode = ErrorCode.COURSE_NOT_FOUND;
  public CourseNotFoundException(Long courseId) {
        super("Course not found with id: " + courseId);
  }
  public CourseNotFoundException(String message) {
    super(message);
  }
  public CourseNotFoundException() {
    super("Course not found.");
  }
}
