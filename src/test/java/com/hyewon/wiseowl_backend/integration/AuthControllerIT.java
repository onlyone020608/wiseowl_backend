package com.hyewon.wiseowl_backend.integration;

import com.hyewon.wiseowl_backend.domain.auth.client.GoogleOAuthClient;
import com.hyewon.wiseowl_backend.domain.auth.dto.*;
import com.hyewon.wiseowl_backend.domain.auth.entity.RefreshToken;
import com.hyewon.wiseowl_backend.domain.auth.repository.RefreshTokenRepository;
import com.hyewon.wiseowl_backend.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerIT extends AbstractIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @MockitoBean
    private GoogleOAuthClient googleOAuthClient;

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
    @DisplayName("POST /api/auth/login - returns access & refresh tokens when credentials are valid")
    void login_withValidCredentials_returnsTokens() throws Exception {
        LoginRequest request = new LoginRequest("test@example.com", "encoded-password");

        mockMvc.perform(post("/api/auth/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/auth/oauth/google - registers new user and returns tokens when social account does not exist")
    void loginWithGoogle_whenNewUser_registersUserAndReturnsTokens() throws Exception {
        // given
        String authCode = "dummy-auth-code";
        OAuthLoginRequest request = new OAuthLoginRequest(authCode);

        given(googleOAuthClient.getToken(authCode))
                .willReturn(new GoogleTokenResponse("access-token", 3600L, null, "scope", "id-token", "Bearer"));
        given(googleOAuthClient.getUserInfo("access-token"))
                .willReturn(new GoogleUserInfo("google-sub-new", "new@example.com", "new-user"));

        // when & then
        mockMvc.perform(post("/api/auth/oauth/google")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.newUser").value(true));
    }

    @Test
    @DisplayName("POST /api/auth/oauth/google - returns tokens when social account exists")
    void loginWithGoogle_whenExistingUser_returnsTokens() throws Exception {
        // given
        String authCode = "dummy-auth-code";
        OAuthLoginRequest request = new OAuthLoginRequest(authCode);

        given(googleOAuthClient.getToken(authCode))
                .willReturn(new GoogleTokenResponse("access-token", 3600L, null, "scope", "id-token", "Bearer"));
        given(googleOAuthClient.getUserInfo("access-token"))
                .willReturn(new GoogleUserInfo("google-sub", "test@example.com", "test-user"));

        // when & then
        mockMvc.perform(post("/api/auth/oauth/google")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.newUser").value(false));
    }

    @Test
    @DisplayName("PATCH /api/auth/password - updates password when current password is valid")
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
    @DisplayName("POST /api/auth/refresh - returns new access & refresh tokens when refresh token is valid")
    void refreshAccessToken_withValidRefreshToken_returnsNewTokens() throws Exception {
        String refreshToken = testDataLoader.getRefreshToken();

        mockMvc.perform(post("/api/auth/refresh")
                        .header("Authorization", refreshToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    @DisplayName("POST /api/auth/logout - deletes stored refresh token and logs user out")
    void logout_withValidRefreshToken_deletesStoredToken() throws Exception {
        // given
        User user = testDataLoader.getTestUser();
        String accessToken = jwtProvider.generateAccessToken(user.getEmail());
        String refreshToken = jwtProvider.generateRefreshToken(user.getEmail());

        refreshTokenRepository.save(new RefreshToken(user.getEmail(), refreshToken));

        // when & then
        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer " + accessToken)
                        .header("Refresh-Token", refreshToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertThat(refreshTokenRepository.findByEmail(user.getEmail())).isEmpty();
    }
}
