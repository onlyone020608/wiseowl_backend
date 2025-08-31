package com.hyewon.wiseowl_backend.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponse {
    private final String accessToken;
    private final String refreshToken;
    boolean newUser;
}
