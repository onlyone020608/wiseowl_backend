package com.hyewon.wiseowl_backend.global.exception;

import lombok.Getter;

@Getter
public class FacilityNotFoundException extends RuntimeException {
    private final ErrorCode errorCode = ErrorCode.FACILITY_NOT_FOUND;
    public FacilityNotFoundException(String message) {
        super(message);
    }
    public FacilityNotFoundException() {
        super("No facilities are registered in the system.");
    }


}
