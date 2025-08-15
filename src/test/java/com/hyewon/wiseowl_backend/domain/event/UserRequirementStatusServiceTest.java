package com.hyewon.wiseowl_backend.domain.event;

import com.hyewon.wiseowl_backend.domain.course.entity.Major;
import com.hyewon.wiseowl_backend.domain.course.repository.MajorRepository;
import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorRequirement;
import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;
import com.hyewon.wiseowl_backend.domain.requirement.service.MajorRequirementQueryService;
import com.hyewon.wiseowl_backend.domain.user.dto.UserMajorTypeUpdateRequest;
import com.hyewon.wiseowl_backend.domain.user.dto.UserMajorUpdateRequest;
import com.hyewon.wiseowl_backend.domain.user.entity.Profile;
import com.hyewon.wiseowl_backend.domain.user.entity.User;
import com.hyewon.wiseowl_backend.domain.user.entity.UserMajor;
import com.hyewon.wiseowl_backend.domain.user.entity.UserRequirementStatus;
import com.hyewon.wiseowl_backend.domain.user.repository.ProfileRepository;
import com.hyewon.wiseowl_backend.domain.user.repository.UserMajorRepository;
import com.hyewon.wiseowl_backend.domain.user.repository.UserRepository;
import com.hyewon.wiseowl_backend.domain.user.repository.UserRequirementStatusRepository;
import com.hyewon.wiseowl_backend.domain.user.service.UserRequirementStatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserRequirementStatusServiceTest {
    @InjectMocks private UserRequirementStatusService userRequirementStatusService;
    @Mock private UserMajorRepository userMajorRepository;
    @Mock private MajorRequirementQueryService majorRequirementQueryService;
    @Mock private UserRepository userRepository;
    @Mock private UserRequirementStatusRepository userRequirementStatusRepository;
    @Mock private ProfileRepository profileRepository;
    @Mock private MajorRepository majorRepository;

    private User user;
    private Major major;
    private Major oldMajor;
    private MajorRequirement majorRequirement;
    private Profile profile;
    private UserMajor userMajor;
    private UserRequirementStatus userRequirementStatus;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .build();
        oldMajor = Major.builder()
                .id(9L)
                .build();
        major = Major.builder()
                .id(10L)
                .build();
        majorRequirement = MajorRequirement.builder()
                .major(major)
                .majorType(MajorType.PRIMARY)
                .build();
        profile = Profile.builder()
                .entranceYear(2025)
                .build();
        userMajor = UserMajor.builder()
                .id(20L)
                .user(user)
                .major(major)
                .build();
        userRequirementStatus = UserRequirementStatus.builder()
                .majorRequirement(majorRequirement)
                .build();
    }

    @Test
    @DisplayName("replaceUserRequirementStatusWithMajor - should replace previous user requirement status when event is received")
    void replaceUserRequirementStatus_shouldReplaceUserRequirementStatus_WithMajor_whenEventReceived() {
        // given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(majorRequirementQueryService.getApplicableRequirements(major.getId(), majorRequirement.getMajorType(), 2025))
                .willReturn(List.of(majorRequirement));
        given(profileRepository.findByUserId(userId)).willReturn(Optional.of(profile));
        given(majorRepository.findById(9L)).willReturn(Optional.of(oldMajor));
        List<UserRequirementStatus> existingStatuses = List.of(userRequirementStatus);
        given(userRequirementStatusRepository.findByUserAndMajor(userId, oldMajor, MajorType.PRIMARY))
                .willReturn(existingStatuses);

        given(majorRequirementQueryService.getApplicableRequirements(10L, MajorType.PRIMARY, 2025))
                .willReturn(List.of(majorRequirement));

        // when
        userRequirementStatusService.replaceUserRequirementStatusWithMajor(userId,
                List.of(new UserMajorUpdateRequest(MajorType.PRIMARY, oldMajor.getId(), major.getId())));

        // then
        verify(userRequirementStatusRepository).deleteAll(existingStatuses);
    }

    @Test
    @DisplayName("replaceUserRequirementStatusWithMajorType - should replace previous user requirement status when event is received")
    void replaceUserRequirementStatus_shouldReplaceUserRequirementStatus_WithMajorType_whenEventReceived() {
        // given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userMajorRepository.findById(20L)).willReturn(Optional.of(userMajor));
        given(profileRepository.findByUserId(userId)).willReturn(Optional.of(profile));
        List<UserRequirementStatus> existingStatuses = List.of(userRequirementStatus);
        given(userRequirementStatusRepository.findByUserAndMajor(userId, userMajor.getMajor(), MajorType.PRIMARY))
                .willReturn(existingStatuses);
        given(majorRequirementQueryService.getApplicableRequirements(
                major.getId(),
                MajorType.DOUBLE,
                profile.getEntranceYear()
        )).willReturn(List.of(majorRequirement));

        // when
        userRequirementStatusService.replaceUserRequirementStatusWithMajorType(userId,
                List.of(new UserMajorTypeUpdateRequest(userMajor.getId(), MajorType.PRIMARY, MajorType.DOUBLE)));

        // then
        verify(userRequirementStatusRepository).deleteAll(existingStatuses);
    }
}
