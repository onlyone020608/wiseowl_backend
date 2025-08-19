package com.hyewon.wiseowl_backend.global.exception;

import lombok.Getter;

@Getter
public class RequirementNotFoundException extends RuntimeException {
  private final ErrorCode errorCode = ErrorCode.REQUIRED_MAJOR_COURSE_NOT_FOUND;

  public RequirementNotFoundException(String message) {
        super(message);
  }

  public RequirementNotFoundException(Long requirementId) {
    super("Requirement not found with id: " + requirementId);
  }
}
