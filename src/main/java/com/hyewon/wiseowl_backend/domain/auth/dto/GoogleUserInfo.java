package com.hyewon.wiseowl_backend.domain.auth.dto;

public record GoogleUserInfo(
        String sub,
        String email,
        String name
) {
}
