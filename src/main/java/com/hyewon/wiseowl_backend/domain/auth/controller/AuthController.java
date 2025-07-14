package com.hyewon.wiseowl_backend.domain.auth.controller;

import com.hyewon.wiseowl_backend.domain.auth.dto.ChangePasswordRequest;
import com.hyewon.wiseowl_backend.domain.auth.dto.LoginRequest;
import com.hyewon.wiseowl_backend.domain.auth.dto.TokenResponse;
import com.hyewon.wiseowl_backend.domain.auth.dto.SignUpRequest;
import com.hyewon.wiseowl_backend.domain.auth.security.UserPrincipal;
import com.hyewon.wiseowl_backend.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping
    public ResponseEntity<Void> signUp(@RequestBody SignUpRequest request) {
        authService.signup(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        TokenResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal UserPrincipal principal, ChangePasswordRequest request){
        authService.changePassword(principal.getId(), request);
        return ResponseEntity.ok().build();
    }
}
