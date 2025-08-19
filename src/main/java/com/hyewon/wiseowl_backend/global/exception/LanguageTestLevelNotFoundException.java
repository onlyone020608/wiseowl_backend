package com.hyewon.wiseowl_backend.global.exception;

import lombok.Getter;

@Getter
public class LanguageTestLevelNotFoundException extends RuntimeException {
    private final ErrorCode errorCode = ErrorCode.LANGUAGE_TEST_LEVEL_NOT_FOUND;

    public LanguageTestLevelNotFoundException(String message) {
        super(message);
    }

    public LanguageTestLevelNotFoundException(Long languageTestLevelId) {
        super("Language test level not found with id: " + languageTestLevelId);
    }
}
