package com.hyewon.wiseowl_backend.global.exception;

import lombok.Getter;

@Getter
public class EmailAlreadyExistsException extends RuntimeException {
  private final ErrorCode errorCode = ErrorCode.EMAIL_ALREADY_EXISTS;
    public EmailAlreadyExistsException(String message) {
        super(message);
    }

  public EmailAlreadyExistsException() {
    super("Email already exists");
  }
}
