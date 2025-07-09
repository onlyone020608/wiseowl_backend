package com.hyewon.wiseowl_backend.domain;

import com.hyewon.wiseowl_backend.domain.course.entity.Course;
import com.hyewon.wiseowl_backend.domain.course.entity.CourseOffering;
import com.hyewon.wiseowl_backend.domain.course.entity.CourseType;
import com.hyewon.wiseowl_backend.domain.course.entity.Major;
import com.hyewon.wiseowl_backend.domain.course.repository.CourseOfferingRepository;
import com.hyewon.wiseowl_backend.domain.course.repository.MajorRepository;
import com.hyewon.wiseowl_backend.domain.requirement.entity.CreditRequirement;
import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorRequirement;
import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;
import com.hyewon.wiseowl_backend.domain.requirement.entity.Requirement;
import com.hyewon.wiseowl_backend.domain.requirement.repository.CreditRequirementRepository;
import com.hyewon.wiseowl_backend.domain.requirement.repository.MajorRequirementRepository;
import com.hyewon.wiseowl_backend.domain.user.dto.*;
import com.hyewon.wiseowl_backend.domain.user.entity.*;
import com.hyewon.wiseowl_backend.domain.user.repository.*;
import com.hyewon.wiseowl_backend.domain.user.service.UserService;
import com.hyewon.wiseowl_backend.global.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    @Mock
    private ProfileRepository profileRepository;
    @Mock
    private MajorRepository majorRepository;
    @Mock
    private UserMajorRepository userMajorRepository;
    @Mock
    private UserCompletedCourseRepository userCompletedCourseRepository;
    @Mock
    private CourseOfferingRepository courseOfferingRepository;
    @Mock
    private MajorRequirementRepository majorRequirementRepository;
    @Mock
    private UserRequirementStatusRepository userRequirementStatusRepository;
    @Mock
    private CreditRequirementRepository creditRequirementRepository;


    private User user;
    private Profile profile;
    private Major major;
    private MajorRequirement mr1;
    private UserRequirementStatus urs1;
    private Requirement requirement;
    private CourseOffering offering;
    private CompletedCourseUpdateRequest updateRequest;
    private UserMajor userMajor;
    private CreditRequirement creditRequirement;
    private UserCompletedCourse ucc;
    private Course course;
    private ProfileUpdateRequest profileUpdateRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("test@test.com")
                .build();
        major = Major.builder()
                .name("컴퓨터공학과")
                .build();
        profile = Profile.builder()
                .user(user)
                .build();
        requirement = Requirement.builder()
                .name("졸업시험")
                .build();
        mr1 = MajorRequirement.builder()
                .major(major)
                .requirement(requirement)
                .build();
        offering = mock(CourseOffering.class);
        urs1 = mock(UserRequirementStatus.class);
        userMajor = mock(UserMajor.class);
        creditRequirement = mock(CreditRequirement.class);
        ucc = mock(UserCompletedCourse.class);
        course = mock(Course.class);
        CompletedCourseUpdateItem item =
                new CompletedCourseUpdateItem(1L, Grade.A, false);
        profileUpdateRequest = new ProfileUpdateRequest(
                "test",
                2022,
                List.of(new UserMajorRequest(1L, MajorType.PRIMARY))
        );

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
        given(majorRequirementRepository.findApplicable(major.getId(), MajorType.PRIMARY, profileUpdateRequest.entranceYear()))
                .willReturn(List.of(mr1));

        // when
        userService.updateUserProfile(userId, profileUpdateRequest);

        // then

        assertThat(profile.getEntranceYear()).isEqualTo(2022);
        assertThat(user.getUsername()).isEqualTo("test");

        ArgumentCaptor<UserMajor> userMajorCaptor = ArgumentCaptor.forClass(UserMajor.class);
        verify(userMajorRepository).save(userMajorCaptor.capture());

        UserMajor savedUserMajor = userMajorCaptor.getValue();
        assertThat(savedUserMajor.getUser()).isEqualTo(user);
        assertThat(savedUserMajor.getMajor()).isEqualTo(major);
        assertThat(savedUserMajor.getMajorType()).isEqualTo(MajorType.PRIMARY);

        verify(userRequirementStatusRepository).saveAll(argThat(iterable -> {
            List<UserRequirementStatus> list = StreamSupport
                    .stream(iterable.spliterator(), false)
                    .toList();

            return list.size() == 1 &&
                    list.get(0).getMajorRequirement().equals(mr1) &&
                    list.get(0).getUser().equals(user);
        }));
    }


    @Test
    @DisplayName("updateUserProfile - should throw when user not found")
    void updateUserProfile_shouldThrow_whenUserNotFound() {
        // given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class,
                () -> userService.updateUserProfile(userId, profileUpdateRequest));
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
                () -> userService.updateUserProfile(userId, profileUpdateRequest));
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
                () -> userService.updateUserProfile(userId, profileUpdateRequest));
    }
}
//
//    @Test
//    @DisplayName("insertCompletedCourses – success")
//    void insertCompletedCourses_success() {
//        // given
//        Long userId = 1L;
//        given(userRepository.findById(userId)).willReturn(Optional.of(user));
//        given(userCompletedCourseRepository.existsByUserId(userId)).willReturn(false);
//        given(courseOfferingRepository.findById(1L)).willReturn(Optional.of(offering));
//
//        // when
//        userService.insertCompletedCourses(userId, updateRequest);
//
//        // then
//        verify(userCompletedCourseRepository).saveAll(any());
//
//    }
//
//    @Test
//    @DisplayName("insertCompletedCourses – already exists")
//    void insertCompletedCourses_alreadyExists() {
//        // given
//        Long userId = 1L;
//        given(userRepository.findById(userId)).willReturn(Optional.of(user));
//        given(userCompletedCourseRepository.existsByUserId(userId)).willReturn(true);
//
//        // when & then
//        assertThrows(CompletedCourseAlreadyExistsException.class,
//                () -> userService.insertCompletedCourses(userId, updateRequest));
//    }
//
//    @Test
//    @DisplayName("insertCompletedCourses – courseOffering not found")
//    void insertCompletedCourses_offeringNotFound() {
//        // given
//        Long userId = 1L;
//        given(userRepository.findById(userId)).willReturn(Optional.of(user));
//        given(userCompletedCourseRepository.existsByUserId(userId)).willReturn(false);
//        given(courseOfferingRepository.findById(1L)).willReturn(Optional.empty());
//
//        // when & then
//        assertThrows(CourseOfferingNotFoundException.class,
//                () -> userService.insertCompletedCourses(userId, updateRequest));
//    }
//
//    @Test
//    @DisplayName("getGraduationRequirementsForUser - should group by major and map to response")
//    void getGraduationRequirementsForUser_shouldSucceed(){
//        // given
//        Long userId = 1L;
//        given(major.getId()).willReturn(1L);
//        given(major.getName()).willReturn("컴퓨터공학과");
//
//        given(mr1.getMajor()).willReturn(major);
//        given(mr1.getMajorType()).willReturn(MajorType.PRIMARY);
//        given(urs1.getMajorRequirement()).willReturn(mr1);
//        given(mr1.getRequirement()).willReturn(requirement);
//        List<UserRequirementStatus> mockStatuses = List.of(urs1);
//        given(userRequirementStatusRepository.findAllByUserId(userId))
//                .willReturn(mockStatuses);
//
//        // when
//        List<GraduationRequirementGroupByMajorResponse> result =
//                userService.getGraduationRequirementsForUser(userId);
//
//
//        // then
//        assertThat(result).hasSize(1);
//        GraduationRequirementGroupByMajorResponse dto = result.get(0);
//
//        assertThat(dto.majorId()).isEqualTo(1L);
//        assertThat(dto.majorName()).isEqualTo("컴퓨터공학과");
//        assertThat(dto.majorType()).isEqualTo(MajorType.PRIMARY);
//        assertThat(dto.requirements()).hasSize(1);
//
//    }
//
//    @Test
//    @DisplayName("getGraduationRequirementsForUser - should throw when no data found")
//    void getGraduationRequirementsForUser_shouldThrow_whenNoDataFound() {
//        // given
//        Long userId = 999L;
//        given(userRequirementStatusRepository.findAllByUserId(userId))
//                .willReturn(List.of());
//
//        // when & then
//        assertThatThrownBy(() -> userService.getGraduationRequirementsForUser(userId))
//                .isInstanceOf(UserGraduationStatusNotFoundException.class);
//    }
//
//    @Test
//    @DisplayName("updateUserRequirementStatus - should update userRequirementStatus")
//    void updateUserRequirementStatus() {
//        // given
//        Long userId = 1L;
//        given(userRequirementStatusRepository.findById(0L)).willReturn(Optional.of(urs1));
//
//        // when
//        RequirementStatusUpdate update = new RequirementStatusUpdate(urs1.getId(), true);
//        UserRequirementFulfillmentRequest rfRequest = new UserRequirementFulfillmentRequest(major.getId(), List.of(update));
//        userService.updateUserRequirementStatus(userId, rfRequest);
//
//        // then
//        verify(urs1).updateFulfilled(true);
//
//
//
//    }
//
//    @Test
//    @DisplayName("updateUserRequirementStatus - should throw when user requirement status not found")
//    void updateUserRequirementStatus_userRequirementStatusNotFound() {
//        // given
//        Long userId = 1L;
//        given(userRequirementStatusRepository.findById(0L)).willReturn(Optional.empty());
//
//        // when & then
//        RequirementStatusUpdate update = new RequirementStatusUpdate(urs1.getId(), true);
//        UserRequirementFulfillmentRequest rfRequest = new UserRequirementFulfillmentRequest(major.getId(), List.of(update));
//        assertThrows(UserGraduationStatusNotFoundException.class,
//                () -> userService.updateUserRequirementStatus(userId, rfRequest));
//
//    }
//
//
//
//}
