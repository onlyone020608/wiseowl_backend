package com.hyewon.wiseowl_backend.domain;

import com.hyewon.wiseowl_backend.domain.auth.dto.ChangePasswordRequest;
import com.hyewon.wiseowl_backend.domain.auth.dto.LoginRequest;
import com.hyewon.wiseowl_backend.domain.auth.dto.SignUpRequest;
import com.hyewon.wiseowl_backend.domain.auth.dto.TokenResponse;
import com.hyewon.wiseowl_backend.domain.auth.entity.RefreshToken;
import com.hyewon.wiseowl_backend.domain.auth.repository.RefreshTokenRepository;
import com.hyewon.wiseowl_backend.domain.auth.security.JwtProvider;
import com.hyewon.wiseowl_backend.domain.auth.security.UserPrincipal;
import com.hyewon.wiseowl_backend.domain.auth.service.AuthService;
import com.hyewon.wiseowl_backend.domain.user.entity.User;
import com.hyewon.wiseowl_backend.domain.user.repository.UserRepository;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    @Mock private  UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private Authentication authentication;
    @Mock private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private AuthService authService;

    private SignUpRequest signUpRequest;
    private LoginRequest loginRequest;
    private User user;
    private UserPrincipal userPrincipal;

    @BeforeEach
    void setUp() {
        signUpRequest = new SignUpRequest("tester@email.com", "securepass");
        loginRequest = new LoginRequest("tester@email.com", "rawPassword");
//        user = User.of("tester@email.com", "encodedPassword");
        user = User.builder()
                .id(1L)
                .username("Test")
                .email("tester@email.com")
                .password("encodedPassword")
                .build();
        userPrincipal = new UserPrincipal(user);

    }

    @Test
    @DisplayName("signup - successful registration")
    void signup_shouldSucceed() {
        // given
        given(userRepository.existsByEmail(signUpRequest.getEmail())).willReturn(false);

        // when
        authService.signup(signUpRequest);

        // then
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("signup - throws exception when email is already in use")
    void signup_shouldThrowWhenEmailAlreadyExists() {
        // given
        given(userRepository.existsByEmail(signUpRequest.getEmail())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.signup(signUpRequest))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("login - returns access and refresh token when credentials are valid")
    void login_shouldReturnTokens_whenCredentialsAreValid() {
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
    @DisplayName("login - should fail with bad credentials")
    void login_shouldFail_whenCredentialsInvalid() {
        // given
        given(authenticationManager.authenticate(any()))
                .willThrow(new BadCredentialsException("Invalid credentials"));

        // when & then
        assertThrows(BadCredentialsException.class,
                () -> authService.login(loginRequest));
    }

    @Test
    @DisplayName("changePassword - successfully updates user's password")
    void changePassword_shouldSucceed() {
        // given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.matches("encodedPassword", user.getPassword()))
                .willReturn(true);
        given(passwordEncoder.encode("newPassword")).willReturn("encodedNewPassword");

        // when
        ChangePasswordRequest request = new ChangePasswordRequest("encodedPassword", "newPassword");
        authService.changePassword(user.getId(),request);

        // then
        assertThat(user.getPassword()).isEqualTo("encodedNewPassword");

    }

    @Test
    @DisplayName("changePassword - should throw UserNotFoundException when user does not exist")
    void changePassword_shouldThrow_whenUserNotFound() {
        // given
        Long userId = 999L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());
        // when
        ChangePasswordRequest request = new ChangePasswordRequest("encodedPassword", "newPassword");

        // when & then
        assertThatThrownBy(() -> authService.changePassword(999L, request))
                .isInstanceOf(UserNotFoundException.class);

    }

    @Test
    @DisplayName("changePassword - should throw InvalidCurrentPasswordException when current password is incorrect")
    void changePassword_shouldThrow_whenCurrentPasswordIncorrect() {
        // given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.matches("wrongPassword", user.getPassword())).willReturn(false);

        ChangePasswordRequest request = new ChangePasswordRequest("wrongPassword", "newPassword");

        // when & then
        assertThatThrownBy(() -> authService.changePassword(userId, request))
                .isInstanceOf(InvalidCurrentPasswordException.class)
                .hasMessage("Current password is incorrect.");
    }

    @Test
    @DisplayName("refresh - returns new access token when refresh token is valid")
    void refresh_shouldReturnNewAccessToken_whenValid() {
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
    @DisplayName("refresh - throws exception when token is invalid")
    void refresh_shouldThrowException_whenTokenInvalid() {
        // given
        String refreshToken = "invalidToken";
        given(jwtProvider.validateToken(refreshToken)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.refresh(refreshToken))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid refresh token");
    }

    @Test
    @DisplayName("refresh - throws exception when token does not match stored token")
    void refresh_shouldThrowException_whenTokenMismatch() {
        // given
        String refreshToken = "validToken";
        String savedToken = "differentToken";
        String email = "test@test.com";

        RefreshToken stored = new RefreshToken(email, savedToken);

        given(jwtProvider.validateToken(refreshToken)).willReturn(true);
        given(jwtProvider.getEmailFromToken(refreshToken)).willReturn(email);
        given(refreshTokenRepository.findByEmail(email)).willReturn(Optional.of(stored));

        // when & then
        assertThatThrownBy(() -> authService.refresh(refreshToken))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Refresh token mismatch");
    }









}
