package com.hyewon.wiseowl_backend.global.exception;

import lombok.Getter;

@Getter
public class ProfileNotFoundException extends RuntimeException {
    private final ErrorCode errorCode = ErrorCode.PROFILE_NOT_FOUND;

    public ProfileNotFoundException(Long profileId) {
        super("Profile not found with id: " + profileId);
    }
    public ProfileNotFoundException(String message) {
        super(message);
    }
    public ProfileNotFoundException() {
        super("Profile not found.");
    }
}