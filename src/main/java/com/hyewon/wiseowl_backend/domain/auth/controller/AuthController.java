package com.hyewon.wiseowl_backend.domain.auth.controller;

import com.hyewon.wiseowl_backend.domain.auth.dto.ChangePasswordRequest;
import com.hyewon.wiseowl_backend.domain.auth.dto.LoginRequest;
import com.hyewon.wiseowl_backend.domain.auth.dto.SignUpRequest;
import com.hyewon.wiseowl_backend.domain.auth.dto.TokenResponse;
import com.hyewon.wiseowl_backend.domain.auth.security.UserPrincipal;
import com.hyewon.wiseowl_backend.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<TokenResponse> signUp(@RequestBody SignUpRequest request) {
        TokenResponse response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        TokenResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal UserPrincipal principal, @RequestBody ChangePasswordRequest request) {
        authService.changePassword(principal.getId(), request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshAccessToken(@RequestHeader("Authorization") String refreshToken) {
        TokenResponse response = authService.refresh(refreshToken);
        return ResponseEntity.ok(response);
    }
}
