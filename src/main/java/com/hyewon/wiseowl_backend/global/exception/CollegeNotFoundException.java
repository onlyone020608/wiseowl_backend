package com.hyewon.wiseowl_backend.global.exception;

import lombok.Getter;

@Getter
public class CollegeNotFoundException extends RuntimeException {
  private final ErrorCode errorCode = ErrorCode.COLLEGE_NOT_FOUND;

  public CollegeNotFoundException(String message) {
        super(message);
  }

  public CollegeNotFoundException(Long collegeId) {
    super("College not found with id: " + collegeId);
  }
}
