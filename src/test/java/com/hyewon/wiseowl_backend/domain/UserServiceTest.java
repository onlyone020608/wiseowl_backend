package com.hyewon.wiseowl_backend.domain;

import com.hyewon.wiseowl_backend.domain.course.entity.Major;
import com.hyewon.wiseowl_backend.domain.course.repository.MajorRepository;
import com.hyewon.wiseowl_backend.domain.requirement.MajorType;
import com.hyewon.wiseowl_backend.domain.user.dto.ProfileUpdateRequest;
import com.hyewon.wiseowl_backend.domain.user.dto.UserMajorRequest;
import com.hyewon.wiseowl_backend.domain.user.entity.Profile;
import com.hyewon.wiseowl_backend.domain.user.entity.User;
import com.hyewon.wiseowl_backend.domain.user.entity.UserMajor;
import com.hyewon.wiseowl_backend.domain.user.repository.ProfileRepository;
import com.hyewon.wiseowl_backend.domain.user.repository.UserMajorRepository;
import com.hyewon.wiseowl_backend.domain.user.repository.UserRepository;
import com.hyewon.wiseowl_backend.domain.user.service.UserService;
import com.hyewon.wiseowl_backend.global.exception.MajorNotFoundException;
import com.hyewon.wiseowl_backend.global.exception.ProfileNotFoundException;
import com.hyewon.wiseowl_backend.global.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    private UserRepository userRepository;
    @Mock private ProfileRepository profileRepository;
    @Mock private MajorRepository majorRepository;
    @Mock private UserMajorRepository userMajorRepository;

    private ProfileUpdateRequest request;
    private User user;
    private Profile profile;
    private Major major;


    @BeforeEach
    void setUp() {
        request = new ProfileUpdateRequest(
                "test",
                2022,
                List.of(new UserMajorRequest(1L, MajorType.PRIMARY))
        );
        user = mock(User.class);
        profile = mock(Profile.class);
        major = mock(Major.class);
    }

    @Test
    @DisplayName("updateUserProfile - should update user and profile and save userMajor")
    void updateUserProfile_shouldSucceed() {
        // given
        Long userId = 1L;

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(profileRepository.findByUserId(userId)).willReturn(Optional.of(profile));
        given(majorRepository.findById(1L)).willReturn(Optional.of(major));

        // when
        userService.updateUserProfile(userId, request);

        // then
        verify(user).updateUsername("test");
        verify(profile).updateEntranceYear(2022);
        verify(userMajorRepository).save(any(UserMajor.class));
    }

    @Test
    @DisplayName("updateUserProfile - should throw when user not found")
    void updateUserProfile_shouldThrow_whenUserNotFound() {
        // given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class,
                () -> userService.updateUserProfile(userId, request));
    }

    @Test
    @DisplayName("updateUserProfile - should throw when profile not found")
    void updateUserProfile_shouldThrow_whenProfileNotFound() {
        // given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(profileRepository.findByUserId(userId)).willReturn(Optional.empty());

        // when & then
        assertThrows(ProfileNotFoundException.class,
                () -> userService.updateUserProfile(userId, request));
    }

    @Test
    @DisplayName("updateUserProfile - should throw when major not found")
    void updateUserProfile_shouldThrow_whenMajorNotFound() {
        // given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(profileRepository.findByUserId(userId)).willReturn(Optional.of(profile));
        given(majorRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThrows(MajorNotFoundException.class,
                () -> userService.updateUserProfile(userId, request));
    }
}
