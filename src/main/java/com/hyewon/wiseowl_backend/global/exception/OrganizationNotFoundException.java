package com.hyewon.wiseowl_backend.global.exception;

import lombok.Getter;

@Getter
public class OrganizationNotFoundException extends RuntimeException {
    private final ErrorCode errorCode = ErrorCode.ORGANIZATION_NOT_FOUND;

    public OrganizationNotFoundException(String message) {
        super(message);
    }
    public OrganizationNotFoundException(Long organizationId) {
        super("Organization not found with id " + organizationId);
    }
    public OrganizationNotFoundException() {
        super("Organization not found.");
    }
}
