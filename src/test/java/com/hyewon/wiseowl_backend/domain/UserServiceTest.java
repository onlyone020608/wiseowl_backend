package com.hyewon.wiseowl_backend.domain;

import com.hyewon.wiseowl_backend.domain.course.entity.*;
import com.hyewon.wiseowl_backend.domain.course.service.CourseOfferingQueryService;
import com.hyewon.wiseowl_backend.domain.course.service.MajorQueryService;
import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorRequirement;
import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;
import com.hyewon.wiseowl_backend.domain.requirement.entity.RequiredLiberalCategory;
import com.hyewon.wiseowl_backend.domain.requirement.entity.Track;
import com.hyewon.wiseowl_backend.domain.requirement.service.CreditRequirementQueryService;
import com.hyewon.wiseowl_backend.domain.user.dto.*;
import com.hyewon.wiseowl_backend.domain.user.entity.*;
import com.hyewon.wiseowl_backend.domain.user.event.*;
import com.hyewon.wiseowl_backend.domain.user.repository.*;
import com.hyewon.wiseowl_backend.domain.user.service.UserService;
import com.hyewon.wiseowl_backend.fixture.CourseFixture;
import com.hyewon.wiseowl_backend.fixture.RequirementFixture;
import com.hyewon.wiseowl_backend.fixture.UserFixture;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private static final Logger log = LoggerFactory.getLogger(UserServiceTest.class);

    @InjectMocks UserService userService;
    @Mock private UserRepository userRepository;
    @Mock private ProfileRepository profileRepository;
    @Mock private MajorQueryService majorQueryService;
    @Mock private UserMajorRepository userMajorRepository;
    @Mock private UserCompletedCourseRepository userCompletedCourseRepository;
    @Mock private CourseOfferingQueryService courseOfferingQueryService;
    @Mock private UserRequirementStatusRepository userRequirementStatusRepository;
    @Mock private CreditRequirementQueryService creditRequirementQueryService;
    @Mock private UserRequiredCourseStatusRepository userRequiredCourseStatusRepository;
    @Mock private UserSubscriptionRepository userSubscriptionRepository;
    @Mock private UserTrackRepository userTrackRepository;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private EntityManager entityManager;

    private User user;
    private Profile profile;
    private Major major;
    private Major major2;
    private MajorRequirement mr1;
    private UserRequirementStatus urs1;
    private CourseOffering offering;
    private CompletedCourseInsertRequest completedCourseInsertRequest;
    private UserMajor userMajor;
    private UserMajor userMajor2;
    private UserCompletedCourse ucc;
    private UserCompletedCourse ucc2;
    private Course course;
    private College college;
    private LiberalCategory liberalCategory;
    private RequiredLiberalCategory rlc;
    private ProfileUpdateRequest profileUpdateRequest;
    private UserRequiredCourseStatus userRequiredCourseStatus1;
    private UserRequiredCourseStatus userRequiredCourseStatus2;
    private UserTrack userTrack;

    @BeforeEach
    void setUp() {
        profile = Profile.builder()
                .gpa(3.9)
                .entranceYear(2024)
                .build();
        user = UserFixture.aUserWithProfile(profile);
        college = CourseFixture.aCollege();
        major = CourseFixture.aMajor1(college);
        major2 = CourseFixture.aMajor2(college);
        mr1 = RequirementFixture.aMajorRequirement(major);
        urs1 = UserRequirementStatus.builder()
                .id(20L)
                .user(user)
                .majorRequirement(mr1)
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
        liberalCategory = CourseFixture.aLiberalCategory();
        course = CourseFixture.aMajorCourse(major);
        offering = CourseFixture.aMajorCourseOffering(course);
        ucc = UserCompletedCourse.builder()
                .id(1L)
                .user(user)
                .courseOffering(offering)
                .grade(Grade.A)
                .retake(true)
                .build();
        ucc2 = UserCompletedCourse.builder()
                .id(2L)
                .user(user)
                .courseOffering(offering)
                .grade(Grade.A)
                .retake(true)
                .build();
        profileUpdateRequest = new ProfileUpdateRequest(
                "test",
                2022,
                List.of(new UserMajorRequest(1L, MajorType.PRIMARY)),
                Track.PRIMARY_WITH_DOUBLE
        );
        userTrack = UserTrack.builder()
                .user(user)
                .track(Track.PRIMARY_WITH_DOUBLE)
                .build();
        rlc = RequirementFixture.aRequiredLiberalCategory(major);
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
    @DisplayName("updates user's profile when profile update request is valid")
    void shouldUpdateUserProfile_whenProfileUpdateRequestValid() {
        // given
        Long userId = 1L;
        Profile profile = Profile.builder().user(user).build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(profileRepository.findByUserId(userId)).willReturn(Optional.of(profile));
        given(majorQueryService.getMajor(1L)).willReturn(major);

        // when
        userService.updateUserProfile(userId, profileUpdateRequest);

        // then
        assertThat(profile.getEntranceYear()).isEqualTo(2022);
        assertThat(user.getUsername()).isEqualTo("test");

        ArgumentCaptor<UserMajor> userMajorCaptor = ArgumentCaptor.forClass(UserMajor.class);
        ArgumentCaptor<UserTrack> userTrackCaptor = ArgumentCaptor.forClass(UserTrack.class);
        verify(userMajorRepository).save(userMajorCaptor.capture());
        verify(userTrackRepository).save(userTrackCaptor.capture());

        UserMajor savedUserMajor = userMajorCaptor.getValue();
        assertThat(savedUserMajor.getUser()).isEqualTo(user);
        assertThat(savedUserMajor.getMajor().getName()).isEqualTo("컴퓨터공학과");
        assertThat(savedUserMajor.getMajorType()).isEqualTo(MajorType.PRIMARY);

        UserTrack savedUserTrack = userTrackCaptor.getValue();
        assertThat(savedUserTrack.getUser()).isEqualTo(user);
        assertThat(savedUserTrack.getTrack()).isEqualTo(Track.PRIMARY_WITH_DOUBLE);

        verify(eventPublisher).publishEvent(any(UserMajorRegisteredEvent.class));
    }

    @Test
    @DisplayName("throws UserNotFoundException when user does not exist in updateUserProfile")
    void shouldThrowException_whenUserNotFoundInUpdateUserProfile() {
        // given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class,
                () -> userService.updateUserProfile(userId, profileUpdateRequest));
    }

    @Test
    @DisplayName("throws ProfileNotFoundException when profile does not exist in updateUserProfile")
    void shouldThrowException_whenProfileNotFound() {
        // given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(profileRepository.findByUserId(userId)).willReturn(Optional.empty());

        // when & then
        assertThrows(ProfileNotFoundException.class,
                () -> userService.updateUserProfile(userId, profileUpdateRequest));
    }

    @Test
    @DisplayName("saves completed courses when completed course insert request is valid")
    void shouldSaveCompletedCourses_whenCompletedCourseInsertRequestValid() {
        // given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userCompletedCourseRepository.existsByUserIdAndCourseOffering_Id(userId, 1L)).willReturn(false);
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
    @DisplayName("throws CompletedCourseAlreadyExistsException when completed course already exists")
    void shouldThrowException_whenCompletedCourseAlreadyExists() {
        // given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userCompletedCourseRepository.existsByUserIdAndCourseOffering_Id(userId, 1L)).willReturn(true);

        // when & then
        assertThrows(CompletedCourseAlreadyExistsException.class,
                () -> userService.insertCompletedCourses(userId, completedCourseInsertRequest));
    }

    @Test
    @DisplayName("returns graduation requirements grouped by major when user has requirements")
    void shouldReturnGraduationRequirements_whenUserHasRequirements() {
        // given
        Long userId = 1L;
        given(userRequirementStatusRepository.findAllByUserIdWithMajor(userId))
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
    @DisplayName("throws UserRequirementStatusNotFoundException when user has no requirements")
    void shouldThrowException_whenUserHasNoRequirements() {
        // given
        Long userId = 999L;
        given(userRequirementStatusRepository.findAllByUserIdWithMajor(userId))
                .willReturn(List.of());

        // when & then
        assertThatThrownBy(() -> userService.getGraduationRequirementsForUser(userId))
                .isInstanceOf(UserRequirementStatusNotFoundException.class);
    }

    @Test
    @DisplayName("updates userRequirementStatus when request is valid")
    void shouldUpdateUserRequirementStatus_whenUserRequirementFulfillmentRequestValid() {
        // given
        Long userId = 1L;
        given(userRequirementStatusRepository.findById(20L)).willReturn(Optional.of(urs1));

        // when
        UserRequirementFulfillmentRequest rfRequest = new UserRequirementFulfillmentRequest(urs1.getId(), true);
        userService.updateUserRequirementStatus(userId, List.of(rfRequest));

        // then
        assertThat(urs1.isFulfilled()).isEqualTo(true);
    }

    @Test
    @DisplayName("throws UserRequirementStatusNotFoundException when user requirement status does not exist")
    void shouldThrowException_whenUserRequirementStatusNotFound() {
        // given
        Long userId = 1L;
        given(userRequirementStatusRepository.findById(urs1.getId())).willReturn(Optional.empty());

        // when & then
        UserRequirementFulfillmentRequest rfRequest = new UserRequirementFulfillmentRequest(urs1.getId(), true);
        assertThrows(UserRequirementStatusNotFoundException.class,
                () -> userService.updateUserRequirementStatus(userId, List.of(rfRequest)));
    }

    @Test
    @DisplayName("returns graduation overview for each major when user has majors and requirementsr")
    void shouldReturnUserGraduationOverview_whenUserHasMajorsAndRequirements() {
        //given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userMajorRepository.findAllByUserIdWithMajor(userId)).willReturn(List.of(userMajor));
        given(userTrackRepository.findByUserId(userId)).willReturn(userTrack);
        given(userRequirementStatusRepository.findByUserAndMajor(userId, major, MajorType.PRIMARY)).willReturn(List.of(urs1));
        given(creditRequirementQueryService.sumRequiredCredits(major, MajorType.PRIMARY, Track.PRIMARY_WITH_DOUBLE, 2024)).willReturn(130);
        given(userCompletedCourseRepository.sumCreditsByUserAndMajor(userId, major.getId())).willReturn(3);

        // when
        MainPageGraduationStatusResponse response = userService.getUserGraduationOverview(userId);

        // then
        RequirementStatusByMajor byMajor = response.requirementStatuses().get(0);
        assertThat(response.username()).isEqualTo("Test");
        assertThat(response.requirementStatuses().size()).isEqualTo(1);
        assertThat(byMajor.majorName()).isEqualTo("컴퓨터공학과");
        assertThat(byMajor.requiredCredits()).isEqualTo(130);
        assertThat(byMajor.earnedCredits()).isEqualTo(3);
        assertThat(byMajor.requirements().get(0).fulfilled()).isEqualTo(false);
        assertThat(byMajor.requirements().get(0).name()).isEqualTo("졸업시험");
    }

    @Test
    @DisplayName("throws UserNotFoundException when user does not exist in getUserGraduationOverview")
    void shouldThrowException_whenUserNotFoundInGetGraduationOverview() {
        // given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class,
                () -> userService.getUserGraduationOverview(userId));
    }

    @Test
    @DisplayName("throws UserMajorNotFoundException when user has no majors in getUserGraduationOverview")
    void shouldThrowException_whenUserHasNoMajor() {
        // given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userMajorRepository.findAllByUserIdWithMajor(userId)).willReturn(List.of());

        // when & then
        assertThrows(UserMajorNotFoundException.class,
                () -> userService.getUserGraduationOverview(userId));
    }

    @Test
    @DisplayName("returns required major and liberal course statuses when user has majors")
    void shouldReturnUserRequiredCourseStatus_whenUserHasMajor() {
        // given
        Long userId = 1L;
        given(userRequiredCourseStatusRepository.findMajorItems(userId, MajorType.PRIMARY))
                .willReturn(List.of(new MajorRequiredCourseItemResponse(course.getCourseCodePrefix(), course.getName(), userRequiredCourseStatus1.isFulfilled())));
        given(userRequiredCourseStatusRepository.findLiberalItems(userId))
                .willReturn(List.of(new LiberalRequiredCourseItemResponse(liberalCategory.getName(), userRequiredCourseStatus2.isFulfilled(), rlc.getRequiredCredit())));

        // when
        UserRequiredCourseStatusResponse response = userService.getUserRequiredCourseStatus(userId, MajorType.PRIMARY);

        // then
        assertThat(response.majorRequiredCourses().get(0).courseCode()).isEqualTo("V41006");
        assertThat(response.majorRequiredCourses().get(0).courseName()).isEqualTo("자료구조");
        assertThat(response.majorRequiredCourses().get(0).fulfilled()).isEqualTo(false);

        assertThat(response.liberalRequiredCourses().get(0).liberalCategoryName()).isEqualTo("언어와문학");
        assertThat(response.liberalRequiredCourses().get(0).requiredCredit()).isEqualTo(6);
        assertThat(response.liberalRequiredCourses().get(0).fulfilled()).isEqualTo(false);
    }

    @Test
    @DisplayName("returns user summary including primary and double major when user has majors")
    void shouldReturnUserSummary_whenUserHasMajor() {
        // given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userTrackRepository.findByUserId(userId)).willReturn(userTrack);
        given(userMajorRepository.findUserMajorWithCollege(userId, MajorType.PRIMARY)).willReturn(
                Optional.of(new UserMajorDetail(userMajor.getId(), college.getId(), college.getName(), major.getId(), major.getName(), MajorType.PRIMARY)));
        given(userMajorRepository.findUserMajorWithCollege(userId, MajorType.DOUBLE))
                .willReturn(Optional.of(new UserMajorDetail(userMajor2.getId(), college.getId(), college.getName(), major2.getId(), major2.getName(), MajorType.DOUBLE)));
        given(userMajorRepository.existsByUserIdAndMajorType(userId, MajorType.DOUBLE)).willReturn(true);
        given(userMajorRepository.existsByUserIdAndMajorType(userId, MajorType.MINOR)).willReturn(false);

        // when
        UserSummaryResponse response = userService.getUserSummary(userId);

        // then
        assertThat(response.username()).isEqualTo("Test");
        assertThat(response.primaryMajor().majorName()).isEqualTo("컴퓨터공학과");
        assertThat(response.doubleMajor().majorName()).isEqualTo("전기전자공학과");
        assertThat(response.gpa()).isEqualTo(3.9);
    }

    @Test
    @DisplayName("throws UserNotFoundException when user does not exist in getUserSummary")
    void shouldThrowException_whenUserNotFoundInGetUserSummary() {
        // given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class,
                () -> userService.getUserSummary(userId));
    }

    @Test
    @DisplayName("updates user majors when request is valid")
    void shouldUpdateUserMajor_whenUserMajorUpdateRequestValid() {
        // given
        Long userId = 1L;
        given(userMajorRepository.findByUserIdAndMajorType(userId, MajorType.PRIMARY))
                .willReturn(userMajor);
        given(userMajorRepository.findByUserIdAndMajorType(userId, MajorType.DOUBLE))
                .willReturn(userMajor2);
        given(majorQueryService.getMajor(1L)).willReturn(major);
        given(majorQueryService.getMajor(2L)).willReturn(major2);

        // when
        UserMajorUpdateRequest rq1 = new UserMajorUpdateRequest(MajorType.PRIMARY, 1L, 2L);
        UserMajorUpdateRequest rq2 = new UserMajorUpdateRequest(MajorType.DOUBLE, 2L, 1L);
        userService.updateUserMajor(userId, List.of(rq1, rq2));

        // then
        assertThat(userMajor.getMajor()).isEqualTo(major2);
        assertThat(userMajor2.getMajor()).isEqualTo(major);
        verify(eventPublisher).publishEvent(any(UserMajorUpdateEvent.class));
    }

    @Test
    @DisplayName("updates user major types when request is valid")
    void shouldUpdateUserMajorTypes_whenUserMajorTypeRequestValid() {
        // given
        Long userId = 1L;
        given(userMajorRepository.findById(userMajor.getId()))
                .willReturn(Optional.of(userMajor));
        given(userMajorRepository.findById(userMajor2.getId()))
                .willReturn(Optional.of(userMajor2));
        given(userTrackRepository.findByUserId(userId))
                .willReturn(userTrack);

        // when
        UserMajorTypeUpdateItem item1 = new UserMajorTypeUpdateItem(1L, MajorType.PRIMARY, MajorType.DOUBLE);
        UserMajorTypeUpdateItem item2 = new UserMajorTypeUpdateItem(2L, MajorType.DOUBLE, MajorType.PRIMARY);
        UserMajorTypeUpdateRequest request = new UserMajorTypeUpdateRequest(List.of(item1, item2), Track.PRIMARY_WITH_DOUBLE);

        userService.updateUserMajorTypes(userId, request);

        // then
        assertThat(userMajor.getMajorType()).isEqualTo(MajorType.DOUBLE);
        assertThat(userMajor2.getMajorType()).isEqualTo(MajorType.PRIMARY);
        verify(eventPublisher).publishEvent(any(UserMajorTypeUpdateEvent.class));
    }

    @Test
    @DisplayName("throws UserMajorNotFoundException when user major does not exist in updateUserMajorTypes")
    void shouldThrowException_whenUserMajorNotFound() {
        // given
        Long userId = 1L;
        given(userMajorRepository.findById(userMajor.getId()))
                .willReturn(Optional.empty());

        UserMajorTypeUpdateItem item1 = new UserMajorTypeUpdateItem(1L, MajorType.PRIMARY, MajorType.DOUBLE);
        UserMajorTypeUpdateItem item2 = new UserMajorTypeUpdateItem(2L, MajorType.DOUBLE, MajorType.PRIMARY);
        UserMajorTypeUpdateRequest request = new UserMajorTypeUpdateRequest(List.of(item1, item2), Track.PRIMARY_WITH_DOUBLE);

        // when & then
        assertThrows(UserMajorNotFoundException.class,
                () -> userService.updateUserMajorTypes(userId, request));
    }

    @Test
    @DisplayName("returns grouped completed courses when user has completed courses")
    void shouldReturnGroupedCompletedCourses_whenUserHasCompletedCourses() {
        // given
        Long userId = 1L;
        given(userCompletedCourseRepository.findAllByUserIdWithCourseOffering(userId))
                .willReturn(List.of(ucc, ucc2));

        // when
        List<UserCompletedCourseBySemesterResponse> result = userService.getUserCompletedCourses(userId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().year()).isEqualTo(2024);
        assertThat(result.getFirst().term()).isEqualTo(Term.FIRST);
    }

    @Test
    @DisplayName("updates completed course when request is valid")
    void shouldUpdateCompletedCourses_whenCompletedCourseUpdateRequestValid() {
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
    @DisplayName("throws UserCompletedCourseNotFoundException when completed course does not exist")
    void shouldThrowException_whenUserCompletedCourseNotFound() {
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
    @DisplayName("registers user subscriptions when request is valid" )
    void shouldSaveUserSubscriptions_whenUserSubscriptionRequestValid() {
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
    @DisplayName("throws UserNotFoundException when user does not exist in registerUserSubscriptions" )
    void shouldThrowException_whenUserNotFoundInRegisterUserSubscriptions() {
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
    @DisplayName("replaces all user subscriptions when request is valid" )
    void shouldUpdateAllUserSubscriptions_whenUserSubscriptionRequestValid() {
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
                        tuple(2L, SubscriptionType.ORGANIZATION));
        assertThat(saved).allSatisfy(subscription ->
                assertThat(subscription.getUser()).isEqualTo(user)
        );
    }

    @Test
    @DisplayName("throws UserNotFoundException when user does not exist in replaceAllUserSubscriptions" )
    void shouldThrowException_whenUserNotFoundInReplaceAllUserSubscriptions() {
        // given
        Long userId = 999L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());
        UserSubscriptionRequest request1 = new UserSubscriptionRequest(1L, SubscriptionType.MAJOR);
        UserSubscriptionRequest request2 = new UserSubscriptionRequest(2L, SubscriptionType.ORGANIZATION);

        // when & then
        assertThrows(UserNotFoundException.class, () ->
                userService.replaceAllUserSubscriptions(userId,  List.of(request1, request2)));
    }

    @DisplayName("marks user as deleted when user exists")
    @Test
    void shouldMarkUserAsDeleted_whenIdExists() {
        // given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        userService.deleteUser(userId);

        // then
        assertThat(user.isDeleted()).isTrue();
    }

    @DisplayName("throws UserNotFoundException when user does not exist in deleteUser")
    @Test
    void shouldThrowException_whenUserNotFoundInDeleteUser() {
        // given
        Long userId = 999L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class, () ->
                userService.deleteUser(userId));
    }
}
