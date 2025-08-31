package com.hyewon.wiseowl_backend.domain.auth.controller;

public record GoogleUserInfo(
        String sub,
        String email,
        String name
) {
}
