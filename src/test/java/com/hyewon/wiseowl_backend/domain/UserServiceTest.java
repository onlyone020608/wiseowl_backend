package com.hyewon.wiseowl_backend.domain;

import com.hyewon.wiseowl_backend.domain.course.entity.*;
import com.hyewon.wiseowl_backend.domain.course.repository.CourseOfferingRepository;
import com.hyewon.wiseowl_backend.domain.course.repository.MajorRepository;
import com.hyewon.wiseowl_backend.domain.requirement.entity.*;
import com.hyewon.wiseowl_backend.domain.requirement.repository.CreditRequirementRepository;
import com.hyewon.wiseowl_backend.domain.requirement.repository.MajorRequirementRepository;
import com.hyewon.wiseowl_backend.domain.requirement.repository.RequiredLiberalCategoryByCollegeRepository;
import com.hyewon.wiseowl_backend.domain.requirement.repository.RequiredMajorCourseRepository;
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
import static org.mockito.Mockito.*;

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
    @Mock
    private RequiredMajorCourseRepository requiredMajorCourseRepository;

    @Mock
    private RequiredLiberalCategoryByCollegeRepository requiredLiberalCategoryByCollegeRepository;

    @Mock
    private UserRequiredCourseStatusRepository userRequiredCourseStatusRepository;


    private User user;
    private Profile profile;
    private Major major;
    private MajorRequirement mr1;
    private UserRequirementStatus urs1;
    private Requirement requirement;
    private CourseOffering offering;
    private CompletedCourseUpdateRequest completedCourseUpdateRequest;
    private UserMajor userMajor;
    private CreditRequirement creditRequirement;
    private UserCompletedCourse ucc;
    private Course course;
    private College college;
    private RequiredMajorCourse requiredMajorCourse;
    private RequiredLiberalCategoryByCollege rlc;
    private ProfileUpdateRequest profileUpdateRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .username("Test")
                .email("test@test.com")
                .build();
        college = College.builder().build();
        major = Major.builder()
                .id(1L)
                .name("컴퓨터공학과")
                .college(college)
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
                .majorType(MajorType.PRIMARY)
                .build();
        urs1 = UserRequirementStatus.builder()
                .id(20L)
                .user(user)
                .majorRequirement(mr1)
                .fulfilled(false)
                .build();
        userMajor = UserMajor.builder()
                .user(user)
                .major(major)
                .majorType(MajorType.PRIMARY)
                .build();
        creditRequirement = mock(CreditRequirement.class);

        course = Course.builder()
                .major(major)
                .name("자료구조")
                .credit(3)
                .courseType(CourseType.MAJOR)
                .build();
        offering = CourseOffering.builder()
                .course(course)
                .build();

        ucc = UserCompletedCourse.builder()
                .user(user)
                .courseOffering(offering)
                .grade(Grade.A)
                .build();
        profileUpdateRequest = new ProfileUpdateRequest(
                "test",
                2022,
                List.of(new UserMajorRequest(1L, MajorType.PRIMARY))
        );

        creditRequirement = CreditRequirement.builder()
                .major(major)
                .majorType(MajorType.PRIMARY)
                .courseType(CourseType.MAJOR)
                .requiredCredits(130)
                .build();

        requiredMajorCourse = RequiredMajorCourse.builder()
                .major(major)
                .majorType(MajorType.PRIMARY)
                .build();

        rlc =RequiredLiberalCategoryByCollege.builder()
                .build();

        CompletedCourseUpdateItem item =
                new CompletedCourseUpdateItem(1L, Grade.A, true);
        completedCourseUpdateRequest = new CompletedCourseUpdateRequest(List.of(item));

    }

    @Test
    @DisplayName("updateUserProfile - should update user and profile and save userMajor, userRequirementStatus, userRequiredCourseStatus")
    void updateUserProfile_shouldSucceed() {
        // given
        Long userId = 1L;

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(profileRepository.findByUserId(userId)).willReturn(Optional.of(profile));
        given(majorRepository.findById(1L)).willReturn(Optional.of(major));
        given(majorRequirementRepository.findApplicable(major.getId(), MajorType.PRIMARY, profileUpdateRequest.entranceYear()))
                .willReturn(List.of(mr1));
        given(requiredMajorCourseRepository.findApplicableMajorCourses(major.getId(), MajorType.PRIMARY, profileUpdateRequest.entranceYear()))
                .willReturn(List.of(requiredMajorCourse));
        given(requiredLiberalCategoryByCollegeRepository.findApplicableLiberalCategories(major.getCollege().getId(),  profileUpdateRequest.entranceYear()))
                .willReturn(List.of(rlc));


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

        verify(userRequiredCourseStatusRepository).saveAll(argThat(iterable -> {
            List<UserRequiredCourseStatus> list = StreamSupport.stream(iterable.spliterator(), false).toList();

            return list.size() == 1 &&
                    list.get(0).getCourseType() == CourseType.MAJOR &&
                    list.get(0).getUser().equals(user);
        }));

        verify(userRequiredCourseStatusRepository).saveAll(argThat(iterable -> {
            List<UserRequiredCourseStatus> list = StreamSupport.stream(iterable.spliterator(), false).toList();

            return list.size() == 1 &&
                    list.get(0).getCourseType() == CourseType.GENERAL &&
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

    @Test
    @DisplayName("insertCompletedCourses – success")
    void insertCompletedCourses_success() {
        // given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userCompletedCourseRepository.existsByUserId(userId)).willReturn(false);
        given(courseOfferingRepository.findById(1L)).willReturn(Optional.of(offering));

        // when
        userService.insertCompletedCourses(userId, completedCourseUpdateRequest);

        // then
        verify(userCompletedCourseRepository).saveAll(argThat(iterable -> {
            List<UserCompletedCourse> list = StreamSupport.stream(iterable.spliterator(), false)
                    .toList();
            return list.size() == 1 &&
                    list.get(0).getUser().equals(user)
                    && list.get(0).getCourseOffering().equals(offering)
                    && list.get(0).getGrade().equals(Grade.A)
                    && list.get(0).isRetake();

        }));

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
                () -> userService.insertCompletedCourses(userId, completedCourseUpdateRequest));
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
                () -> userService.insertCompletedCourses(userId, completedCourseUpdateRequest));
    }

    @Test
    @DisplayName("getGraduationRequirementsForUser - should group by major and map to response")
    void getGraduationRequirementsForUser_shouldSucceed() {
        // given
        Long userId = 1L;
        given(userRequirementStatusRepository.findAllByUserId(userId))
                .willReturn(List.of(urs1));

        // when
        List<GraduationRequirementGroupByMajorResponse> result =
                userService.getGraduationRequirementsForUser(userId);


        // then
        assertThat(result.get(0).majorName()).isEqualTo("컴퓨터공학과");
        assertThat(result.get(0).majorType()).isEqualTo(MajorType.PRIMARY);
        assertThat(result.get(0).requirements().get(0).userRequirementStatusId()).isEqualTo(20L);
        assertThat(result.get(0).requirements().get(0).name()).isEqualTo("졸업시험");
        assertThat(result.get(0).requirements().get(0).fulfilled()).isEqualTo(false);

    }


    @Test
    @DisplayName("getGraduationRequirementsForUser - should throw when no data found")
    void getGraduationRequirementsForUser_shouldThrow_whenNoDataFound() {
        // given
        Long userId = 999L;
        given(userRequirementStatusRepository.findAllByUserId(userId))
                .willReturn(List.of());

        // when & then
        assertThatThrownBy(() -> userService.getGraduationRequirementsForUser(userId))
                .isInstanceOf(UserGraduationStatusNotFoundException.class);
    }


    @Test
    @DisplayName("updateUserRequirementStatus - should update userRequirementStatus")
    void updateUserRequirementStatus() {
        // given
        Long userId = 1L;
        given(userRequirementStatusRepository.findById(20L)).willReturn(Optional.of(urs1));

        // when
        RequirementStatusUpdate update = new RequirementStatusUpdate(urs1.getId(), true);
        UserRequirementFulfillmentRequest rfRequest = new UserRequirementFulfillmentRequest(major.getId(), List.of(update));
        userService.updateUserRequirementStatus(userId, rfRequest);

        // then
        assertThat(urs1.isFulfilled()).isEqualTo(true);


    }

    @Test
    @DisplayName("updateUserRequirementStatus - should throw when user requirement status not found")
    void updateUserRequirementStatus_userRequirementStatusNotFound() {
        // given
        Long userId = 1L;
        given(userRequirementStatusRepository.findById(urs1.getId())).willReturn(Optional.empty());

        // when & then
        RequirementStatusUpdate update = new RequirementStatusUpdate(urs1.getId(), true);
        UserRequirementFulfillmentRequest rfRequest = new UserRequirementFulfillmentRequest(major.getId(), List.of(update));
        assertThrows(UserGraduationStatusNotFoundException.class,
                () -> userService.updateUserRequirementStatus(userId, rfRequest));

    }

    @Test
    @DisplayName("fetchUserGraduationOverview - should return overview for each major")
    void fetchUserGraduationOverview_success(){
        //given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userMajorRepository.findAllByUserId(userId)).willReturn(List.of(userMajor));
        given(userRequirementStatusRepository.findAllByUserId(userId)).willReturn(List.of(urs1));
        given(creditRequirementRepository.findAllByMajorIdAndMajorType(major.getId(),MajorType.PRIMARY)).willReturn(List.of(creditRequirement));
        given(userCompletedCourseRepository.findByUserId(userId)).willReturn(List.of(ucc));


        // when
        MainPageGraduationStatusResponse response = userService.fetchUserGraduationOverview(userId);

        // then
        RequirementStatusByMajor byMajor = response.requirementStatuses().get(0);
        assertThat(response.userName()).isEqualTo("Test");
        assertThat(response.requirementStatuses().size()).isEqualTo(1);
        assertThat(byMajor.majorName()).isEqualTo("컴퓨터공학과");
        assertThat(byMajor.requiredCredits()).isEqualTo(130);
        assertThat(byMajor.earnedCredits()).isEqualTo(3);
        assertThat(byMajor.requirements().get(0).fulfilled()).isEqualTo(false);
        assertThat(byMajor.requirements().get(0).requirementName()).isEqualTo("졸업시험");

    }

    @Test
    @DisplayName("fetchUserGraduationOverview - should throw when user not found")
    void fetchUserGraduationOverview_shouldThrow_whenUserNotFound() {
        // given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class,
                () -> userService.fetchUserGraduationOverview(userId));
    }

    @Test
    @DisplayName("fetchUserGraduationOverview - should throw when user has no majors")
    void fetchUserGraduationOverview_shouldThrow_whenNoMajors() {
        // given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userMajorRepository.findAllByUserId(userId)).willReturn(List.of());

        // when & then
        assertThrows(UserMajorNotFoundException.class,
                () -> userService.fetchUserGraduationOverview(userId));
    }

    @Test
    @DisplayName("fetchUserGraduationOverview - should throw when credit requirement not found")
    void fetchUserGraduationOverview_shouldThrow_whenNoCreditRequirement() {
        // given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userMajorRepository.findAllByUserId(userId)).willReturn(List.of(userMajor));
        given(userRequirementStatusRepository.findAllByUserId(userId)).willReturn(List.of(urs1));
        given(creditRequirementRepository.findAllByMajorIdAndMajorType(major.getId(), MajorType.PRIMARY)).willReturn(List.of()); // 비워둠

        // when & then
        assertThrows(CreditRequirementNotFoundException.class,
                () -> userService.fetchUserGraduationOverview(userId));
    }


}
