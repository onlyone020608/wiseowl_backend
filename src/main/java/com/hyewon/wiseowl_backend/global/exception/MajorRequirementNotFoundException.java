package com.hyewon.wiseowl_backend.global.exception;

import lombok.Getter;

@Getter
public class MajorRequirementNotFoundException extends RuntimeException {
  private final ErrorCode errorCode = ErrorCode.MAJOR_REQUIREMENT_NOT_FOUND;

  public MajorRequirementNotFoundException(String message) {
        super(message);
  }

  public MajorRequirementNotFoundException(Long majorRequirementId) {
    super("Major requirement not found with id: " + majorRequirementId);
  }
}
