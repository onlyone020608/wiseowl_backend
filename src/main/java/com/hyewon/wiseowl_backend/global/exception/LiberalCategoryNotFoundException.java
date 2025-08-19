package com.hyewon.wiseowl_backend.global.exception;

import lombok.Getter;

@Getter
public class LiberalCategoryNotFoundException extends RuntimeException {
  private final ErrorCode errorCode = ErrorCode.LIBERAL_CATEGORY_NOT_FOUND;

    public LiberalCategoryNotFoundException(Long liberalCategoryId) {
        super("LiberalCategory not found with id: " + liberalCategoryId);

    }

    public LiberalCategoryNotFoundException(String message) {
        super(message);
    }

    public LiberalCategoryNotFoundException() {
        super("Liberal Category not found.");
    }

}
