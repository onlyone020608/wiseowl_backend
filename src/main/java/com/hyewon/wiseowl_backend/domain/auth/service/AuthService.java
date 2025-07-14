package com.hyewon.wiseowl_backend.domain.auth.service;

import com.hyewon.wiseowl_backend.domain.auth.dto.LoginRequest;
import com.hyewon.wiseowl_backend.domain.auth.dto.TokenResponse;
import com.hyewon.wiseowl_backend.domain.auth.dto.SignUpRequest;
import com.hyewon.wiseowl_backend.domain.auth.security.JwtProvider;
import com.hyewon.wiseowl_backend.domain.auth.security.UserPrincipal;
import com.hyewon.wiseowl_backend.domain.user.entity.Profile;
import com.hyewon.wiseowl_backend.domain.user.entity.User;
import com.hyewon.wiseowl_backend.domain.user.repository.UserRepository;
import com.hyewon.wiseowl_backend.global.exception.EmailAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public TokenResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();
        Long userId = userDetails.getId();


        String accessToken = jwtProvider.generateAccessToken(userId);
        String refreshToken = jwtProvider.generateRefreshToken(userId);

        return new TokenResponse(accessToken, refreshToken);
    }

    @Transactional
    public void signup(SignUpRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException();
        }

        User user = User.of(request.getEmail(), passwordEncoder.encode(request.getPassword()));
        Profile profile = Profile.createDefault();
        user.assignProfile(profile);
        userRepository.save(user);
    }
}
