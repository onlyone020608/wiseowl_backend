package com.hyewon.wiseowl_backend.global.exception;

import lombok.Getter;

@Getter
public class CourseOfferingNotFoundException extends RuntimeException {
  private final ErrorCode errorCode = ErrorCode.COURSE_OFFERING_NOT_FOUND;
  public CourseOfferingNotFoundException(String message) {
    super(message);
  }
  public CourseOfferingNotFoundException(Long id) {
    super("CourseOffering not found with id = " + id);
  }
  public CourseOfferingNotFoundException() {
    super("CourseOffering not found");
  }
}
