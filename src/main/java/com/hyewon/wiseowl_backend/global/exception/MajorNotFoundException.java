package com.hyewon.wiseowl_backend.global.exception;

import lombok.Getter;

@Getter
public class MajorNotFoundException extends RuntimeException {
  private final ErrorCode errorCode = ErrorCode.MAJOR_NOT_FOUND;

  public MajorNotFoundException(String message) {
    super(message);
  }
  public MajorNotFoundException(Long majorId) {
    super("Major not found with id: " + majorId);
  }
  public MajorNotFoundException() {
    super("Major not found.");
  }
}
