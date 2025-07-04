package com.hyewon.wiseowl_backend.global.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CustomErrorResponse {
    private final int status;
    private final String error;
    private final String message;
    private final LocalDateTime timestamp;

    public CustomErrorResponse(ErrorCode code) {
        this.status = code.getStatus().value();
        this.error = code.name();
        this.message = code.getMessage();
        this.timestamp = LocalDateTime.now();
    }


}

