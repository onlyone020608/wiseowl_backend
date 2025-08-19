package com.hyewon.wiseowl_backend.global.exception;

import lombok.Getter;

@Getter
public class LanguageTestNotFoundException extends RuntimeException {
    private final ErrorCode errorCode = ErrorCode.LANGUAGE_TEST_NOT_FOUND;

    public LanguageTestNotFoundException(String message) {
        super(message);
    }

    public LanguageTestNotFoundException(Long languageTestId) {
        super("LanguageTest not found with id: " + languageTestId);
    }
}
