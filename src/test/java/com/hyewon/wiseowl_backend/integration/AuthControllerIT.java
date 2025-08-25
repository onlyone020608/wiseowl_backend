package com.hyewon.wiseowl_backend.integration;

import com.hyewon.wiseowl_backend.domain.auth.dto.ChangePasswordRequest;
import com.hyewon.wiseowl_backend.domain.auth.dto.LoginRequest;
import com.hyewon.wiseowl_backend.domain.auth.dto.SignUpRequest;
import com.hyewon.wiseowl_backend.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerIT extends AbstractIntegrationTest {
    @Test
    @DisplayName("POST /api/auth - creates a new user account")
    void signUp_withValidRequest_createsNewUserAccount() throws Exception {
        SignUpRequest request = new SignUpRequest("signupTest@email.com", "signupPassword");

        mockMvc.perform(post("/api/auth")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /api/auth/login - logs in user and returns access & refresh tokens")
    void login_withValidCredentials_returnsTokens() throws Exception {
        LoginRequest request = new LoginRequest("test@example.com", "encoded-password");

        mockMvc.perform(post("/api/auth/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /api/auth/password - change password")
    void changePassword_withValidCurrentAndNewPassword_updatesPassword() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getEmail());

        ChangePasswordRequest request = new ChangePasswordRequest
                ("encoded-password", "new-encoded-password");

        mockMvc.perform(patch("/api/auth/password")
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST /api/auth/refresh - return new access token using refresh token ")
    void refreshAccessToken_withValidRefreshToken_returnsNewTokens() throws Exception {
        String refreshToken = testDataLoader.getRefreshToken();

        mockMvc.perform(post("/api/auth/refresh")
                        .header("Authorization", refreshToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }
}
