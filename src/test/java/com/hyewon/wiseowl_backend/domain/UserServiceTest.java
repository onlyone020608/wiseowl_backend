package com.hyewon.wiseowl_backend.domain;

import com.hyewon.wiseowl_backend.domain.course.entity.CourseOffering;
import com.hyewon.wiseowl_backend.domain.course.entity.Major;
import com.hyewon.wiseowl_backend.domain.course.repository.CourseOfferingRepository;
import com.hyewon.wiseowl_backend.domain.course.repository.MajorRepository;
import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;
import com.hyewon.wiseowl_backend.domain.requirement.repository.MajorRequirementRepository;
import com.hyewon.wiseowl_backend.domain.user.dto.CompletedCourseUpdateItem;
import com.hyewon.wiseowl_backend.domain.user.dto.CompletedCourseUpdateRequest;
import com.hyewon.wiseowl_backend.domain.user.dto.ProfileUpdateRequest;
import com.hyewon.wiseowl_backend.domain.user.dto.UserMajorRequest;
import com.hyewon.wiseowl_backend.domain.user.entity.Grade;
import com.hyewon.wiseowl_backend.domain.user.entity.Profile;
import com.hyewon.wiseowl_backend.domain.user.entity.User;
import com.hyewon.wiseowl_backend.domain.user.entity.UserMajor;
import com.hyewon.wiseowl_backend.domain.user.repository.*;
import com.hyewon.wiseowl_backend.domain.user.service.UserService;
import com.hyewon.wiseowl_backend.global.exception.*;
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
import static org.mockito.ArgumentMatchers.*;
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
    @Mock private UserCompletedCourseRepository userCompletedCourseRepository;
    @Mock private CourseOfferingRepository courseOfferingRepository;
    @Mock private MajorRequirementRepository majorRequirementRepository;
    @Mock private UserRequirementStatusRepository userRequirementStatusRepository;

    private ProfileUpdateRequest request;
    private User user;
    private Profile profile;
    private Major major;
    private CourseOffering offering;
    private CompletedCourseUpdateRequest updateRequest;


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
        offering = mock(CourseOffering.class);
        CompletedCourseUpdateItem item =
                new CompletedCourseUpdateItem(1L, Grade.A, false);
        updateRequest = new CompletedCourseUpdateRequest(List.of(item));

    }

    @Test
    @DisplayName("updateUserProfile - should update user and profile and save userMajor, userRequirementStatus")
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
        verify(majorRequirementRepository).findApplicable(anyLong(), any(), any());
        verify(userRequirementStatusRepository).saveAll(anyList());
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

    @Test
    @DisplayName("insertCompletedCourses – success")
    void insertCompletedCourses_success() {
        // given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userCompletedCourseRepository.existsByUserId(userId)).willReturn(false);
        given(courseOfferingRepository.findById(1L)).willReturn(Optional.of(offering));

        // when
        userService.insertCompletedCourses(userId, updateRequest);

        // then
        verify(userCompletedCourseRepository).saveAll(any());

    }

    @Test
    @DisplayName("insertCompletedCourses – already exists")
    void insertCompletedCourses_alreadyExists() {
        // given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userCompletedCourseRepository.existsByUserId(userId)).willReturn(true);

        // when & then
        assertThrows(CompletedCourseAlreadyExistsException.class,
                () -> userService.insertCompletedCourses(userId, updateRequest));
    }

    @Test
    @DisplayName("insertCompletedCourses – courseOffering not found")
    void insertCompletedCourses_offeringNotFound() {
        // given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userCompletedCourseRepository.existsByUserId(userId)).willReturn(false);
        given(courseOfferingRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThrows(CourseOfferingNotFoundException.class,
                () -> userService.insertCompletedCourses(userId, updateRequest));
    }
}
