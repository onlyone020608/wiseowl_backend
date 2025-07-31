package com.hyewon.wiseowl_backend.domain;

import com.hyewon.wiseowl_backend.domain.course.entity.*;
import com.hyewon.wiseowl_backend.domain.course.repository.CourseOfferingRepository;
import com.hyewon.wiseowl_backend.domain.course.repository.MajorRepository;
import com.hyewon.wiseowl_backend.domain.course.service.CourseOfferingQueryService;
import com.hyewon.wiseowl_backend.domain.course.service.MajorQueryService;
import com.hyewon.wiseowl_backend.domain.requirement.entity.*;
import com.hyewon.wiseowl_backend.domain.requirement.repository.CreditRequirementRepository;
import com.hyewon.wiseowl_backend.domain.requirement.repository.MajorRequirementRepository;
import com.hyewon.wiseowl_backend.domain.requirement.repository.RequiredLiberalCategoryByCollegeRepository;
import com.hyewon.wiseowl_backend.domain.requirement.repository.RequiredMajorCourseRepository;
import com.hyewon.wiseowl_backend.domain.requirement.service.CreditRequirementQueryService;
import com.hyewon.wiseowl_backend.domain.requirement.service.MajorRequirementQueryService;
import com.hyewon.wiseowl_backend.domain.user.dto.*;
import com.hyewon.wiseowl_backend.domain.user.entity.*;
import com.hyewon.wiseowl_backend.domain.user.event.CompletedCoursesRegisteredEvent;
import com.hyewon.wiseowl_backend.domain.user.event.CompletedCoursesUpdateEvent;
import com.hyewon.wiseowl_backend.domain.user.repository.*;
import com.hyewon.wiseowl_backend.domain.user.service.UserService;
import com.hyewon.wiseowl_backend.global.exception.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private static final Logger log = LoggerFactory.getLogger(UserServiceTest.class);
    @InjectMocks
    UserService userService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private ProfileRepository profileRepository;
    @Mock
    private MajorRepository majorRepository;

    @Mock
    private MajorQueryService majorQueryService;
    @Mock
    private UserMajorRepository userMajorRepository;
    @Mock
    private UserCompletedCourseRepository userCompletedCourseRepository;
    @Mock
    private CourseOfferingQueryService courseOfferingQueryService;
    @Mock
    private MajorRequirementQueryService majorRequirementQueryService;
    @Mock
    private UserRequirementStatusRepository userRequirementStatusRepository;
    @Mock
    private CreditRequirementRepository creditRequirementRepository;
    @Mock
    private CreditRequirementQueryService creditRequirementQueryService;
    @Mock
    private RequiredMajorCourseRepository requiredMajorCourseRepository;

    @Mock
    private RequiredLiberalCategoryByCollegeRepository requiredLiberalCategoryByCollegeRepository;

    @Mock
    private UserRequiredCourseStatusRepository userRequiredCourseStatusRepository;

    @Mock
    private UserSubscriptionRepository userSubscriptionRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private EntityManager entityManager;



    private User user;
    private Profile profile;
    private Profile profile2;
    private Major major;
    private Major major2;
    private MajorRequirement mr1;
    private MajorRequirement mr2;
    private UserRequirementStatus urs1;
    private UserRequirementStatus urs2;
    private Requirement requirement;
    private CourseOffering offering;
    private CompletedCourseInsertRequest completedCourseInsertRequest;
    private UserMajor userMajor;
    private UserMajor userMajor2;
    private CreditRequirement creditRequirement;
    private UserCompletedCourse ucc;
    private Course course;
    private College college;
    private LiberalCategory liberalCategory;
    private RequiredMajorCourse requiredMajorCourse;
    private RequiredLiberalCategoryByCollege rlc;
    private ProfileUpdateRequest profileUpdateRequest;
    private UserRequiredCourseStatus userRequiredCourseStatus1;
    private UserRequiredCourseStatus userRequiredCourseStatus2;


    @BeforeEach
    void setUp() {
        profile2 = Profile.builder()
                .GPA(3.9)
                .build();
        user = User.builder()
                .username("Test")
                .profile(profile2)
                .email("test@test.com")
                .build();
        college = College.builder().build();
        major = Major.builder()
                .id(1L)
                .name("컴퓨터공학과")
                .college(college)
                .build();
        major2 = Major.builder()
                .id(2L)
                .name("철학과")
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
                .description("다른시험대체가능")
                .build();
        mr2 = MajorRequirement.builder()
                .major(major)
                .requirement(requirement)
                .majorType(MajorType.DOUBLE)
                .build();
        urs1 = UserRequirementStatus.builder()
                .id(20L)
                .user(user)
                .majorRequirement(mr1)
                .fulfilled(false)
                .build();
        urs2 = UserRequirementStatus.builder()
                .id(20L)
                .user(user)
                .majorRequirement(mr2)
                .fulfilled(false)
                .build();
        userMajor = UserMajor.builder()
                .id(1L)
                .user(user)
                .major(major)
                .majorType(MajorType.PRIMARY)
                .build();
        userMajor2 = UserMajor.builder()
                .id(2L)
                .user(user)
                .major(major2)
                .majorType(MajorType.DOUBLE)
                .build();
        creditRequirement = mock(CreditRequirement.class);

        liberalCategory = LiberalCategory.builder()
                .name("인간과사회")
                .build();

        course = Course.builder()
                .major(major)
                .name("자료구조")
                .courseCodePrefix("V41006")
                .credit(3)
                .courseType(CourseType.MAJOR)
                .build();
        offering = CourseOffering.builder()
                .course(course)
                .build();

        ucc = UserCompletedCourse.builder()
                .id(1L)
                .user(user)
                .courseOffering(offering)
                .grade(Grade.A)
                .retake(true)
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
                .id(10L)
                .course(course)
                .major(major)
                .majorType(MajorType.PRIMARY)
                .build();

        rlc =RequiredLiberalCategoryByCollege.builder()
                .id(20L)
                .liberalCategory(liberalCategory)
                .requiredCredit(6)
                .build();
        userRequiredCourseStatus1 = UserRequiredCourseStatus.builder()
                .user(user)
                .courseType(CourseType.MAJOR)
                .requiredCourseId(10L)
                .fulfilled(false)
                .build();

        userRequiredCourseStatus2 = UserRequiredCourseStatus.builder()
                .id(20L)
                .user(user)
                .courseType(CourseType.GENERAL)
                .requiredCourseId(20L)
                .fulfilled(false)
                .build();





        CompletedCourseInsertItem item =
                new CompletedCourseInsertItem(1L, Grade.A, true);
        completedCourseInsertRequest = new CompletedCourseInsertRequest(List.of(item));

    }

    @Test
    @DisplayName("updateUserProfile - should update user and profile and save userMajor, userRequirementStatus, userRequiredCourseStatus")
    void updateUserProfile_shouldSucceed() {
        // given
        Long userId = 1L;

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(profileRepository.findByUserId(userId)).willReturn(Optional.of(profile));
        given(majorQueryService.getMajor(1L)).willReturn(major);
        given(majorRequirementQueryService.getApplicableRequirements(major.getId(), MajorType.PRIMARY, profileUpdateRequest.entranceYear()))
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
    @DisplayName("insertCompletedCourses – success")
    void insertCompletedCourses_success() {
        // given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userCompletedCourseRepository.existsByUserId(userId)).willReturn(false);
        given(courseOfferingQueryService.getCourseOffering(1L)).willReturn(offering);

        // when
        userService.insertCompletedCourses(userId, completedCourseInsertRequest);

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
        verify(eventPublisher).publishEvent(any(CompletedCoursesRegisteredEvent.class));

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
                () -> userService.insertCompletedCourses(userId, completedCourseInsertRequest));
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
        given(creditRequirementQueryService.getCreditRequirements(major.getId(), MajorType.PRIMARY)).willReturn(List.of(creditRequirement));
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
    @DisplayName("fetchUserRequiredCourseStatus - should return required major and liberal courses status correctly")
    void fetchUserRequiredCourseStatus_success() {
        // given
        Long userId = 1L;
        given(userRequiredCourseStatusRepository.findAllByUserId(userId))
                .willReturn(List.of(userRequiredCourseStatus1, userRequiredCourseStatus2));
        given(requiredMajorCourseRepository.findById(10L))
                .willReturn(Optional.of(requiredMajorCourse));
        given(requiredLiberalCategoryByCollegeRepository.findById(20L))
               .willReturn(Optional.of(rlc));

        // when

        UserRequiredCourseStatusResponse response = userService.fetchUserRequiredCourseStatus(userId, MajorType.PRIMARY);

        // then

        assertThat(response.majorRequiredCourses().get(0).courseCode()).isEqualTo("V41006");
        assertThat(response.majorRequiredCourses().get(0).courseName()).isEqualTo("자료구조");
        assertThat(response.majorRequiredCourses().get(0).fulfilled()).isEqualTo(false);;

        assertThat(response.liberalRequiredCourses().get(0).liberalCategoryName()).isEqualTo("인간과사회");
        assertThat(response.liberalRequiredCourses().get(0).requiredCredit()).isEqualTo(6);
        assertThat(response.liberalRequiredCourses().get(0).fulfilled()).isEqualTo(false);

    }
    @Test
    @DisplayName("fetchUserRequiredCourseStatus - should throw when user required course status not found")
    void fetchUserRequiredCourseStatus_shouldThrow_whenNoUserRequiredCourseStatus() {
        // given
        Long userId = 1L;
        given(userRequiredCourseStatusRepository.findAllByUserId(userId))
                .willReturn(List.of());

        // when & then
        assertThrows(UserRequiredCourseStatusNotFoundException.class,
                () -> userService.fetchUserRequiredCourseStatus(userId, MajorType.PRIMARY));

    }

    @Test
    @DisplayName("fetchUserRequiredCourseStatus - should throw when required major course status not found")
    void fetchUserRequiredCourseStatus_shouldThrow_whenNoRequiredMajorCourse() {
        // given
        Long userId = 1L;
        given(userRequiredCourseStatusRepository.findAllByUserId(userId))
                .willReturn(List.of(userRequiredCourseStatus1, userRequiredCourseStatus2));
        given(requiredMajorCourseRepository.findById(10L))
                .willReturn(Optional.empty());

        // when & then
        assertThrows(RequiredMajorCourseNotFoundException.class,
                () -> userService.fetchUserRequiredCourseStatus(userId, MajorType.PRIMARY));

    }

    @Test
    @DisplayName("fetchUserRequiredCourseStatus - should throw when required liberal category not found")
    void fetchUserRequiredCourseStatus_shouldThrow_whenNoRequiredLiberalCategory() {
        // given
        Long userId = 1L;
        given(userRequiredCourseStatusRepository.findAllByUserId(userId))
                .willReturn(List.of(userRequiredCourseStatus1, userRequiredCourseStatus2));
        given(requiredMajorCourseRepository.findById(10L))
                .willReturn(Optional.of(requiredMajorCourse));
        given(requiredLiberalCategoryByCollegeRepository.findById(20L))
                .willReturn(Optional.empty());

        // when & then
        assertThrows(RequiredLiberalCategoryNotFoundException.class,
                () -> userService.fetchUserRequiredCourseStatus(userId, MajorType.PRIMARY));

    }

    @Test
    @DisplayName("fetchUserGraduationRequirementStatus - should return graduation requirement statuses grouped by major type")
    void fetchUserGraduationRequirementStatus_success(){
        // given
        Long userId = 1L;
        given(userRequirementStatusRepository.findAllByUserId(userId))
                .willReturn(List.of(urs1, urs2));

        // when
        List<UserGraduationRequirementStatusResponse> response = userService.fetchUserGraduationRequirementStatus(userId);

        // then
        assertThat(response.get(0).majorType()).isEqualTo(MajorType.PRIMARY);
        assertThat(response.get(0).graduationRequirementItems().get(0).requirementName()).isEqualTo("졸업시험");
        assertThat(response.get(0).graduationRequirementItems().get(0).description()).isEqualTo("다른시험대체가능");
        assertThat(response.get(0).graduationRequirementItems().get(0).fulfilled()).isEqualTo(false);
        assertThat(response.get(1).majorType()).isEqualTo(MajorType.DOUBLE);


    }

    @Test
    @DisplayName("fetchUserGraduationRequirementStatus -  should throw when user requirement status not found")
    void fetchUserGraduationRequirementStatus_shouldThrow_whenNoUserRequirementStatus(){
        // given
        Long userId = 1L;
        given(userRequirementStatusRepository.findAllByUserId(userId))
                .willReturn(List.of());

        // when & then
        assertThrows(UserGraduationStatusNotFoundException.class,
                () -> userService.fetchUserGraduationRequirementStatus(userId));

    }


    @Test
    @DisplayName("fetchUserSummary -  should return user summary including primary and second major if present")
    void fetchUserSummary_success(){
        // given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userMajorRepository.findByUserIdAndMajorType(userId, MajorType.PRIMARY)).willReturn(userMajor);
        given(userMajorRepository.findByUserIdAndMajorTypeIn(
                userId, List.of(MajorType.DOUBLE, MajorType.MINOR)
        )).willReturn(Optional.of(userMajor2));

        // when
        UserSummaryResponse response = userService.fetchUserSummary(userId);

        // then
        assertThat(response.userName()).isEqualTo("Test");
        assertThat(response.primaryMajor().majorName()).isEqualTo("컴퓨터공학과");
        assertThat(response.doubleMajor().majorName()).isEqualTo("철학과");
        assertThat(response.GPA()).isEqualTo(3.9);


    }

    @Test
    @DisplayName("fetchUserSummary - should throw UserNotFoundException when user does not exist")
    void fetchUserSummary_shouldThrowException_whenUserNotFound(){
        // given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());


        // when & then
        assertThrows(UserNotFoundException.class,
                () -> userService.fetchUserSummary(userId));

    }

    @Test
    @DisplayName("updateUserMajor -  should update user major")
    void updateUserMajor_success(){
        // given
        Long userId = 1L;
        given(userMajorRepository.findByUserIdAndMajorType(userId, MajorType.PRIMARY))
                .willReturn(userMajor);
        given(userMajorRepository.findByUserIdAndMajorType(userId, MajorType.DOUBLE))
                .willReturn(userMajor2);
        given(majorQueryService.getMajor(1L)).willReturn(major);
        given(majorQueryService.getMajor(2L)).willReturn(major2);


        // when
        UserMajorUpdateRequest rq1 = new UserMajorUpdateRequest(MajorType.PRIMARY, 2L);
        UserMajorUpdateRequest rq2 = new UserMajorUpdateRequest(MajorType.DOUBLE, 1L);
        userService.updateUserMajor(userId, List.of(rq1, rq2));

        // then
        assertThat(userMajor.getMajor()).isEqualTo(major2);
        assertThat(userMajor2.getMajor()).isEqualTo(major);

    }


    @Test
    @DisplayName("updateUserMajorTypes -  should update user major types")
    void updateUserMajorTypes_success(){
        // given
        given(userMajorRepository.findById(userMajor.getId()))
                .willReturn(Optional.of(userMajor));
        given(userMajorRepository.findById(userMajor2.getId()))
                .willReturn(Optional.of(userMajor2));

        // when
        UserMajorTypeUpdateRequest rq1 = new UserMajorTypeUpdateRequest(1L, MajorType.DOUBLE);
        UserMajorTypeUpdateRequest rq2 = new UserMajorTypeUpdateRequest(2L, MajorType.PRIMARY);
        userService.updateUserMajorTypes(List.of(rq1, rq2));

        // then
        assertThat(userMajor.getMajorType()).isEqualTo(MajorType.DOUBLE);
        assertThat(userMajor2.getMajorType()).isEqualTo(MajorType.PRIMARY);

    }

    @Test
    @DisplayName("updateUserMajorTypes - should throw UserMajorNotFoundException when user major does not exist")
    void updateUserMajorTypes_shouldThrowException_whenUserMajorNotFound(){
        // given
        given(userMajorRepository.findById(userMajor.getId()))
                .willReturn(Optional.empty());

        UserMajorTypeUpdateRequest rq1 = new UserMajorTypeUpdateRequest(1L, MajorType.DOUBLE);
        UserMajorTypeUpdateRequest rq2 = new UserMajorTypeUpdateRequest(2L, MajorType.PRIMARY);

        // when & then
        assertThrows(UserMajorNotFoundException.class,
                () -> userService.updateUserMajorTypes(List.of(rq1, rq2)));

    }

    @Test
    @DisplayName("updateCompletedCourses -  should update user completed courses")
    void updateCompletedCourses_success(){
        // given
        Long userId = 1L;
        given(userCompletedCourseRepository.findById(1L))
                .willReturn(Optional.of(ucc));

        // when
        CompletedCourseUpdateRequest rq1 = new CompletedCourseUpdateRequest(1L, Grade.B, false);
        userService.updateCompletedCourses(userId, List.of(rq1));

        // then
        assertThat(ucc.getGrade()).isEqualTo(Grade.B);
        assertThat(ucc.isRetake()).isEqualTo(false);
        verify(eventPublisher).publishEvent(any(CompletedCoursesUpdateEvent.class));

    }

    @Test
    @DisplayName("updateCompletedCourses -   should throw UserCompletedCourseNotFoundException when user completed course does not exist")
    void updateCompletedCourses_shouldThrowException_whenUserCompletedCourseNotFound(){
        // given
        Long userId = 1L;
        given(userCompletedCourseRepository.findById(2L))
                .willReturn(Optional.empty());

        // when & then
        CompletedCourseUpdateRequest rq1 = new CompletedCourseUpdateRequest(2L, Grade.B, false);
        assertThrows(UserCompletedCourseNotFoundException.class,
                () -> userService.updateCompletedCourses(userId, List.of(rq1)));

    }

    @Test
    @DisplayName("registerUserSubscriptions - should save user subscriptions for majors and organizations" )
    void registerUserSubscriptions_success(){
        // given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        UserSubscriptionRequest request1 = new UserSubscriptionRequest(1L, SubscriptionType.MAJOR);
        UserSubscriptionRequest request2 = new UserSubscriptionRequest(2L, SubscriptionType.ORGANIZATION);
        ArgumentCaptor<List<UserSubscription>> captor = ArgumentCaptor.forClass(List.class);

        // when

        userService.registerUserSubscriptions(userId,  List.of(request1, request2));

        // then
        verify(userSubscriptionRepository).saveAll(captor.capture());
        List<UserSubscription> saved = captor.getValue();
        assertThat(saved).hasSize(2);
        assertThat(saved)
                .extracting("targetId", "type")
                .containsExactlyInAnyOrder(
                        tuple(1L, SubscriptionType.MAJOR),
                        tuple(2L, SubscriptionType.ORGANIZATION)
                );

        assertThat(saved).allSatisfy(subscription ->
                assertThat(subscription.getUser()).isEqualTo(user)
        );

    }

    @Test
    @DisplayName("registerUserSubscriptions - should throw UserNotFoundException when user does not exist" )
    void registerUserSubscriptions_shouldThrowException_whenUserNotFound(){
        // given
        Long userId = 999L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());
        UserSubscriptionRequest request1 = new UserSubscriptionRequest(1L, SubscriptionType.MAJOR);
        UserSubscriptionRequest request2 = new UserSubscriptionRequest(2L, SubscriptionType.ORGANIZATION);

        // when & then

        assertThrows(UserNotFoundException.class, () ->
                userService.registerUserSubscriptions(userId,  List.of(request1, request2)));



    }

    @Test
    @DisplayName("replaceAllUserSubscriptions - should update user subscriptions for majors and organizations" )
    void replaceAllUserSubscriptions_success(){
        // given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        UserSubscriptionRequest request1 = new UserSubscriptionRequest(1L, SubscriptionType.MAJOR);
        UserSubscriptionRequest request2 = new UserSubscriptionRequest(2L, SubscriptionType.ORGANIZATION);

        ArgumentCaptor<List<UserSubscription>> captor = ArgumentCaptor.forClass(List.class);

        // when
        userService.replaceAllUserSubscriptions(userId, List.of(request1, request2));

        // then

        verify(userSubscriptionRepository).deleteByUserId(userId);
        verify(userSubscriptionRepository).saveAll(captor.capture());

        List<UserSubscription> saved = captor.getValue();

        assertThat(saved).hasSize(2);
        assertThat(saved)
                .extracting("targetId", "type")
                .containsExactlyInAnyOrder(
                        tuple(1L, SubscriptionType.MAJOR),
                        tuple(2L, SubscriptionType.ORGANIZATION)
                );

        assertThat(saved).allSatisfy(subscription ->
                assertThat(subscription.getUser()).isEqualTo(user)
        );


    }

    @Test
    @DisplayName("replaceAllUserSubscriptions - should throw UserNotFoundException when user does not exist" )
    void replaceAllUserSubscriptions_shouldThrowException_whenUserNotFound(){
        // given
        Long userId = 999L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());
        UserSubscriptionRequest request1 = new UserSubscriptionRequest(1L, SubscriptionType.MAJOR);
        UserSubscriptionRequest request2 = new UserSubscriptionRequest(2L, SubscriptionType.ORGANIZATION);

        ArgumentCaptor<List<UserSubscription>> captor = ArgumentCaptor.forClass(List.class);

        // when & then
        assertThrows(UserNotFoundException.class, () ->
                userService.replaceAllUserSubscriptions(userId,  List.of(request1, request2)));


    }

    @DisplayName("deleteUser - marks user as deleted when user exists")
    @Test
    void deleteUser_shouldMarkUserAsDeleted() {
        // given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        userService.deleteUser(userId);

        // then
        assertThat(user.isDeleted()).isTrue();
    }

    @DisplayName("deleteUser - should throw UserNotFoundException when user does not exist")
    @Test
    void deleteUser_shouldThrowException_whenUserNotFound() {
        // given
        Long userId = 999L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());


        // when & then
        assertThrows(UserNotFoundException.class, () ->
                userService.deleteUser(userId));
    }



}
