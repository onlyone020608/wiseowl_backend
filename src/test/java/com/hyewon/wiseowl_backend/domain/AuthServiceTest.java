package com.hyewon.wiseowl_backend.domain;

import com.hyewon.wiseowl_backend.domain.auth.client.GoogleOAuthClient;
import com.hyewon.wiseowl_backend.domain.auth.controller.GoogleUserInfo;
import com.hyewon.wiseowl_backend.domain.auth.dto.*;
import com.hyewon.wiseowl_backend.domain.auth.entity.RefreshToken;
import com.hyewon.wiseowl_backend.domain.auth.repository.RefreshTokenRepository;
import com.hyewon.wiseowl_backend.domain.auth.security.JwtProvider;
import com.hyewon.wiseowl_backend.domain.auth.security.UserPrincipal;
import com.hyewon.wiseowl_backend.domain.auth.service.AuthService;
import com.hyewon.wiseowl_backend.domain.user.entity.AuthProviderType;
import com.hyewon.wiseowl_backend.domain.user.entity.SocialAccount;
import com.hyewon.wiseowl_backend.domain.user.entity.User;
import com.hyewon.wiseowl_backend.domain.user.repository.SocialAccountRepository;
import com.hyewon.wiseowl_backend.domain.user.repository.UserRepository;
import com.hyewon.wiseowl_backend.fixture.UserFixture;
import com.hyewon.wiseowl_backend.global.exception.EmailAlreadyExistsException;
import com.hyewon.wiseowl_backend.global.exception.InvalidCurrentPasswordException;
import com.hyewon.wiseowl_backend.global.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtProvider jwtProvider;
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private Authentication authentication;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private SocialAccountRepository socialAccountRepository;
    @Mock private GoogleOAuthClient googleOAuthClient;
    @InjectMocks private AuthService authService;

    private User user;
    private UserPrincipal userPrincipal;
    private SignUpRequest signUpRequest;
    private LoginRequest loginRequest;
    private ChangePasswordRequest changePasswordRequest;
    private GoogleTokenResponse googleTokenResponse;

    @BeforeEach
    void setUp() {
        user = UserFixture.aDefaultUser();
        userPrincipal = new UserPrincipal(user);
        signUpRequest = new SignUpRequest("tester@email.com", "securepass");
        loginRequest = new LoginRequest("tester@email.com", "rawPassword");
        changePasswordRequest = new ChangePasswordRequest("encodedPassword", "newPassword");
        googleTokenResponse = new GoogleTokenResponse("access-token", 3600L, null, "scope", "id-token", "Bearer");
    }

    @Test
    @DisplayName("signs up user when request is valid")
    void shouldSaveUser_whenSignUpRequestValid() {
        // given
        given(userRepository.existsByEmail(signUpRequest.getEmail())).willReturn(false);
        given(jwtProvider.generateAccessToken(user.getEmail()))
                .willReturn("access-token");
        given(jwtProvider.generateRefreshToken(user.getEmail()))
                .willReturn("refresh-token");

        // when
        TokenResponse response = authService.signup(signUpRequest);

        // then
        verify(userRepository).save(any(User.class));
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
    }

    @Test
    @DisplayName("throws EmailAlreadyExistsException when email is already in use during signup")
    void shouldThrowException_whenEmailAlreadyExists() {
        // given
        given(userRepository.existsByEmail(signUpRequest.getEmail())).willReturn(true);

        // when & then
        assertThrows(EmailAlreadyExistsException.class,
                () -> authService.signup(signUpRequest));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("returns access and refresh tokens when login credentials are valid")
    void shouldReturnAccessAndRefreshTokens_whenCredentialsValid() {
        // given
        given(authentication.getPrincipal()).willReturn(userPrincipal);
        given(authenticationManager.authenticate(any())).willReturn(authentication);
        given(jwtProvider.generateAccessToken(user.getEmail()))
                .willReturn("access-token");
        given(jwtProvider.generateRefreshToken(user.getEmail()))
                .willReturn("refresh-token");

        // when
        TokenResponse response = authService.login(loginRequest);

        // then
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
    }

    @Test
    @DisplayName("throws BadCredentialsException when login credentials are invalid")
    void shouldThrowException_whenCredentialsInvalid() {
        // given
        given(authenticationManager.authenticate(any()))
                .willThrow(new BadCredentialsException("Invalid credentials"));

        // when & then
        assertThrows(BadCredentialsException.class,
                () -> authService.login(loginRequest));
    }

    @Test
    @DisplayName("returns existing user tokens when social account exists")
    void shouldReturnTokens_whenSocialAccountExists() {
        // given
        String authCode = "auth-code";

        SocialAccount existingAccount = SocialAccount.builder().id(1L).user(user).provider(AuthProviderType.GOOGLE).providerId("google-sub").build();
        GoogleUserInfo userInfo = new GoogleUserInfo("google-sub", "test@example.com", "test-user");

        given(googleOAuthClient.getToken(authCode)).willReturn(googleTokenResponse);
        given(googleOAuthClient.getUserInfo("access-token")).willReturn(userInfo);
        given(socialAccountRepository.findByProviderAndProviderId(AuthProviderType.GOOGLE, "google-sub"))
                .willReturn(Optional.of(existingAccount));
        given(jwtProvider.generateAccessToken(user.getEmail())).willReturn("jwt-access");
        given(jwtProvider.generateRefreshToken(user.getEmail())).willReturn("jwt-refresh");

        // when
        TokenResponse response = authService.loginWithGoogle(authCode);

        // then
        assertThat(response.getAccessToken()).isEqualTo("jwt-access");
        assertThat(response.getRefreshToken()).isEqualTo("jwt-refresh");
        assertThat(response.isNewUser()).isFalse();
        verify(socialAccountRepository).findByProviderAndProviderId(AuthProviderType.GOOGLE, "google-sub");
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("registers new user and returns tokens when social account does not exist")
    void shouldRegisterUser_whenSocialAccountDoesNotExist() {
        // given
        String authCode = "auth-code";

        User newUser = User.builder().id(2L).email("new@example.com").username("new-user").build();
        GoogleUserInfo userInfo = new GoogleUserInfo("google-sub", "new@example.com", "new-user");

        given(googleOAuthClient.getToken(authCode)).willReturn(googleTokenResponse);
        given(googleOAuthClient.getUserInfo("access-token")).willReturn(userInfo);
        given(socialAccountRepository.findByProviderAndProviderId(AuthProviderType.GOOGLE, "google-sub"))
                .willReturn(Optional.empty());
        given(userRepository.save(any(User.class))).willReturn(newUser);
        given(jwtProvider.generateAccessToken(newUser.getEmail())).willReturn("jwt-access");
        given(jwtProvider.generateRefreshToken(newUser.getEmail())).willReturn("jwt-refresh");

        // when
        TokenResponse response = authService.loginWithGoogle(authCode);

        // then
        assertThat(response.getAccessToken()).isEqualTo("jwt-access");
        assertThat(response.getRefreshToken()).isEqualTo("jwt-refresh");
        assertThat(response.isNewUser()).isTrue();
        verify(userRepository).save(any(User.class));
        verify(socialAccountRepository).save(any(SocialAccount.class));
    }

    @Test
    @DisplayName("updates password when change password request is valid")
    void shouldUpdatePassword_whenChangePasswordRequestValid() {
        // given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.matches("encodedPassword", user.getPassword()))
                .willReturn(true);
        given(passwordEncoder.encode("newPassword")).willReturn("encodedNewPassword");

        // when
        authService.changePassword(user.getId(), changePasswordRequest);

        // then
        assertThat(user.getPassword()).isEqualTo("encodedNewPassword");
    }

    @Test
    @DisplayName("throws UserNotFoundException when user does not exist during password change")
    void shouldThrowException_whenUserNotFound() {
        // given
        Long userId = 999L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class,
                () -> authService.changePassword(999L, changePasswordRequest));
    }

    @Test
    @DisplayName("throws InvalidCurrentPasswordException when current password is incorrect")
    void shouldThrowException_whenCurrentPasswordIncorrect() {
        // given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.matches("wrongPassword", user.getPassword())).willReturn(false);
        ChangePasswordRequest request = new ChangePasswordRequest("wrongPassword", "newPassword");

        // when & then
        assertThrows(InvalidCurrentPasswordException.class,
                () -> authService.changePassword(userId, request));
    }

    @Test
    @DisplayName("returns new access token when refresh token is valid")
    void shouldReturnNewAccessToken_whenRefreshTokenValid() {
        // given
        String refreshToken = "validRefreshToken";
        String email = "test@test.com";
        String newAccessToken = "newAccessToken";

        RefreshToken storedToken = new RefreshToken(email, refreshToken);

        given(jwtProvider.validateToken(refreshToken)).willReturn(true);
        given(jwtProvider.getEmailFromToken(refreshToken)).willReturn(email);
        given(refreshTokenRepository.findByEmail(email)).willReturn(Optional.of(storedToken));
        given(jwtProvider.generateAccessToken(email)).willReturn(newAccessToken);

        // when
        TokenResponse response = authService.refresh(refreshToken);

        // then
        assertThat(response.getAccessToken()).isEqualTo(newAccessToken);
        assertThat(response.getRefreshToken()).isEqualTo(refreshToken);
    }

    @Test
    @DisplayName("throws exception when refresh token is invalid")
    void shouldThrowException_whenTokenInvalid() {
        // given
        String refreshToken = "invalidToken";
        given(jwtProvider.validateToken(refreshToken)).willReturn(false);

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> authService.refresh(refreshToken));
    }

    @Test
    @DisplayName("throws exception when refresh token does not match stored token")
    void shouldThrowException_whenTokenMismatch() {
        // given
        String refreshToken = "validToken";
        String savedToken = "differentToken";
        String email = "test@test.com";

        RefreshToken stored = new RefreshToken(email, savedToken);

        given(jwtProvider.validateToken(refreshToken)).willReturn(true);
        given(jwtProvider.getEmailFromToken(refreshToken)).willReturn(email);
        given(refreshTokenRepository.findByEmail(email)).willReturn(Optional.of(stored));

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> authService.refresh(refreshToken));
    }
}
