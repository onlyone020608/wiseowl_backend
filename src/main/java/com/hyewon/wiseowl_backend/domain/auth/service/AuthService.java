package com.hyewon.wiseowl_backend.domain.auth.service;

import com.hyewon.wiseowl_backend.domain.auth.client.GoogleOAuthClient;
import com.hyewon.wiseowl_backend.domain.auth.controller.GoogleUserInfo;
import com.hyewon.wiseowl_backend.domain.auth.dto.*;
import com.hyewon.wiseowl_backend.domain.auth.entity.RefreshToken;
import com.hyewon.wiseowl_backend.domain.auth.repository.RefreshTokenRepository;
import com.hyewon.wiseowl_backend.domain.auth.security.JwtProvider;
import com.hyewon.wiseowl_backend.domain.auth.security.UserPrincipal;
import com.hyewon.wiseowl_backend.domain.user.entity.AuthProviderType;
import com.hyewon.wiseowl_backend.domain.user.entity.Profile;
import com.hyewon.wiseowl_backend.domain.user.entity.SocialAccount;
import com.hyewon.wiseowl_backend.domain.user.entity.User;
import com.hyewon.wiseowl_backend.domain.user.repository.SocialAccountRepository;
import com.hyewon.wiseowl_backend.domain.user.repository.UserMajorRepository;
import com.hyewon.wiseowl_backend.domain.user.repository.UserRepository;
import com.hyewon.wiseowl_backend.global.exception.EmailAlreadyExistsException;
import com.hyewon.wiseowl_backend.global.exception.InvalidCurrentPasswordException;
import com.hyewon.wiseowl_backend.global.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final UserMajorRepository userMajorRepository;
    private final GoogleOAuthClient googleOAuthClient;

    @Transactional
    public TokenResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();
        String email = userDetails.getEmail();

        String accessToken = jwtProvider.generateAccessToken(email);
        String refreshToken = jwtProvider.generateRefreshToken(email);

        boolean isNewUser = !(userMajorRepository.existsByUserId(userDetails.getId()));

        return new TokenResponse(accessToken, refreshToken, isNewUser);
    }

    @Transactional
    public TokenResponse signup(SignUpRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException();
        }

        User user = User.of(request.getEmail(), passwordEncoder.encode(request.getPassword()));
        Profile profile = Profile.createDefault();
        user.assignProfile(profile);
        userRepository.save(user);

        String accessToken = jwtProvider.generateAccessToken(request.getEmail());
        String refreshToken = jwtProvider.generateRefreshToken(request.getEmail());

        return new TokenResponse(accessToken, refreshToken, true);
    }

    @Transactional
    public TokenResponse loginWithGoogle(String authCode) {
        GoogleTokenResponse googleToken = googleOAuthClient.getToken(authCode);
        GoogleUserInfo userInfo = googleOAuthClient.getUserInfo(googleToken.accessToken());

        Optional<SocialAccount> optionalAccount =
                socialAccountRepository.findByProviderAndProviderId(AuthProviderType.GOOGLE, userInfo.sub());

        User user;
        boolean newUser = false;

        if (optionalAccount.isPresent()) {
            user = optionalAccount.get().getUser();
        } else {
            user = registerNewUser(userInfo);
            newUser = true;
        }

        String accessToken = jwtProvider.generateAccessToken(user.getEmail());
        String refreshToken = jwtProvider.generateRefreshToken(user.getEmail());

        return new TokenResponse(accessToken, refreshToken, newUser);
    }

    private User registerNewUser(GoogleUserInfo userInfo) {
        User user = User.builder()
                .email(userInfo.email())
                .username(userInfo.name())
                .password(null)
                .build();
        Profile profile = Profile.createDefault();
        user.assignProfile(profile);
        userRepository.save(user);

        SocialAccount socialAccount = SocialAccount.builder()
                .user(user)
                .provider(AuthProviderType.GOOGLE)
                .providerId(userInfo.sub())
                .build();
        socialAccountRepository.save(socialAccount);

        return user;
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        if(!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidCurrentPasswordException();
        }

        String encoded = passwordEncoder.encode(request.getNewPassword());
        user.updatePassword(encoded);
    }

    @Transactional
    public TokenResponse refresh(String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String email = jwtProvider.getEmailFromToken(refreshToken);

        RefreshToken storedToken = refreshTokenRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("No refresh token found for user"));

        if (!storedToken.getToken().equals(refreshToken)) {
            throw new IllegalArgumentException("Refresh token mismatch");
        }

        String newAccessToken = jwtProvider.generateAccessToken(email);
        return new TokenResponse(newAccessToken, refreshToken, false);
    }
}
