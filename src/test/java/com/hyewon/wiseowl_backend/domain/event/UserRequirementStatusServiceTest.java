package com.hyewon.wiseowl_backend.domain.event;

import com.hyewon.wiseowl_backend.domain.course.entity.Major;
import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorRequirement;
import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;
import com.hyewon.wiseowl_backend.domain.requirement.service.MajorRequirementQueryService;
import com.hyewon.wiseowl_backend.domain.user.entity.Profile;
import com.hyewon.wiseowl_backend.domain.user.entity.User;
import com.hyewon.wiseowl_backend.domain.user.entity.UserMajor;
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

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserRequirementStatusServiceTest {
    @InjectMocks private UserRequirementStatusService userRequirementStatusService;
    @Mock private UserMajorRepository userMajorRepository;
    @Mock private MajorRequirementQueryService majorRequirementQueryService;
    @Mock private UserRepository userRepository;
    @Mock private UserRequirementStatusRepository userRequirementStatusRepository;
    @Mock private ProfileRepository profileRepository;

    private User user;
    private Major major;
    private MajorRequirement majorRequirement;
    private Profile profile;
    private UserMajor userMajor;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .build();
        major = Major.builder()
                .id(10L)
                .build();
        majorRequirement = MajorRequirement.builder()
                .major(major)
                .majorType(MajorType.PRIMARY)
                .build();
        profile = Profile.builder()
                .user(user)
                .entranceYear(2025)
                .build();
        userMajor = UserMajor.builder()
                .id(20L)
                .user(user)
                .major(major)
                .majorType(MajorType.PRIMARY)
                .build();
    }

    @Test
    @DisplayName("replaceUserRequirementStatus - should replace previous user requirement status when event is received")
    void replaceUserRequirementStatus_shouldReplaceUserRequirementStatus_whenEventReceived() {
        // given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(profileRepository.findByUserId(userId)).willReturn(Optional.of(profile));
        given(userMajorRepository.findAllByUserId(userId)).willReturn(List.of(userMajor));
        given(majorRequirementQueryService.getApplicableRequirements(major.getId(), majorRequirement.getMajorType(), 2025))
                .willReturn(List.of(majorRequirement));

        // when
        userRequirementStatusService.replaceUserRequirementStatus(userId);

        // then
        verify(userRequirementStatusRepository).deleteAllByUserId(userId);
        verify(userRequirementStatusRepository, times(1)).saveAll(anyList());
    }
}
