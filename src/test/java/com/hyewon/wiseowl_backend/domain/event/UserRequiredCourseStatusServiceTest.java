package com.hyewon.wiseowl_backend.domain.event;

import com.hyewon.wiseowl_backend.domain.course.entity.*;
import com.hyewon.wiseowl_backend.domain.course.repository.LiberalCategoryCourseRepository;
import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;
import com.hyewon.wiseowl_backend.domain.requirement.entity.RequiredLiberalCategoryByCollege;
import com.hyewon.wiseowl_backend.domain.requirement.entity.RequiredMajorCourse;
import com.hyewon.wiseowl_backend.domain.requirement.repository.CourseCreditTransferRuleRepository;
import com.hyewon.wiseowl_backend.domain.requirement.repository.RequiredLiberalCategoryByCollegeRepository;
import com.hyewon.wiseowl_backend.domain.requirement.repository.RequiredMajorCourseRepository;
import com.hyewon.wiseowl_backend.domain.requirement.service.CourseCreditTransferRuleService;
import com.hyewon.wiseowl_backend.domain.requirement.service.RequiredLiberalCategoryQueryService;
import com.hyewon.wiseowl_backend.domain.requirement.service.RequiredMajorCourseQueryService;
import com.hyewon.wiseowl_backend.domain.user.entity.*;
import com.hyewon.wiseowl_backend.domain.user.repository.*;
import com.hyewon.wiseowl_backend.domain.user.service.UserRequiredCourseStatusService;
import com.hyewon.wiseowl_backend.global.exception.RequiredLiberalCategoryNotFoundException;
import com.hyewon.wiseowl_backend.global.exception.RequiredMajorCourseNotFoundException;
import com.hyewon.wiseowl_backend.global.exception.UserRequiredCourseStatusNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserRequiredCourseStatusServiceTest {
    @InjectMocks private UserRequiredCourseStatusService userRequiredCourseStatusService;
    @Mock private UserRequiredCourseStatusRepository userRequiredCourseStatusRepository;
    @Mock private CourseCreditTransferRuleRepository ruleRepository;
    @Mock private RequiredMajorCourseRepository requiredMajorCourseRepository;
    @Mock private RequiredLiberalCategoryByCollegeRepository requiredLiberalCategoryByCollegeRepository;
    @Mock private UserCompletedCourseRepository userCompletedCourseRepository;
    @Mock private LiberalCategoryCourseRepository liberalCategoryCourseRepository;
    @Mock private UserRepository userRepository;
    @Mock private ProfileRepository profileRepository;
    @Mock private UserMajorRepository userMajorRepository;
    @Mock private RequiredMajorCourseQueryService requiredMajorCourseQueryService;
    @Mock private RequiredLiberalCategoryQueryService requiredLiberalCategoryQueryService;
    @Mock private CourseCreditTransferRuleService courseCreditTransferRuleService;

    private User user;
    private UserRequiredCourseStatus userRequiredCourseStatus1;
    private UserRequiredCourseStatus userRequiredCourseStatus2;
    private RequiredMajorCourse requiredMajorCourse;
    private RequiredLiberalCategoryByCollege rlc;
    private LiberalCategory liberalCategory;
    private UserCompletedCourse ucc1;
    private UserCompletedCourse ucc2;
    private UserCompletedCourse ucc3;
    private Course course;
    private Course course2;
    private Course course3;
    private CourseOffering offering1;
    private CourseOffering offering2;
    private CourseOffering offering3;
    private Semester semester;
    private Major major;
    private College college;
    private Profile profile;
    private UserMajor userMajor;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .username("Test")
                .email("test@test.com")
                .build();
        profile = Profile.builder()
                .user(user)
                .entranceYear(2025)
                .build();
        liberalCategory = LiberalCategory.builder()
                .id(1L)
                .name("인간과사회")
                .build();
        rlc = RequiredLiberalCategoryByCollege.builder()
                .id(20L)
                .liberalCategory(liberalCategory)
                .requiredCredit(6)
                .build();
        course = Course.builder()
                .id(1L)
                .name("자료구조")
                .courseCodePrefix("V41006")
                .credit(3)
                .courseType(CourseType.MAJOR)
                .build();
        course2 = Course.builder()
                .id(2L)
                .name("국제관계의이해")
                .courseCodePrefix("U74111")
                .credit(6)
                .courseType(CourseType.GENERAL)
                .build();
        course3 = Course.builder()
                .id(3L)
                .name("알고리즘")
                .courseType(CourseType.MAJOR)
                .build();
        semester = Semester.builder()
                .year(2020)
                .build();
        offering1 = CourseOffering.builder()
                .course(course)
                .semester(semester)
                .build();
        offering2 = CourseOffering.builder()
                .course(course2)
                .semester(semester)
                .build();
        offering3 = CourseOffering.builder()
                .course(course3)
                .semester(semester)
                .build();
        ucc1 = UserCompletedCourse.builder()
                .user(user)
                .courseOffering(offering1)
                .grade(Grade.A)
                .build();
        ucc2 = UserCompletedCourse.builder()
                .user(user)
                .courseOffering(offering2)
                .grade(Grade.A)
                .build();
        ucc3 = UserCompletedCourse.builder()
                .user(user)
                .courseOffering(offering3)
                .build();
        requiredMajorCourse = RequiredMajorCourse.builder()
                .id(10L)
                .course(course)
                .majorType(MajorType.PRIMARY)
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
        college = College.builder()
                .id(1L)
                .build();
        major = Major.builder()
                .id(1L)
                .college(college)
                .build();
        userMajor = UserMajor.builder()
                .major(major)
                .user(user)
                .majorType(MajorType.PRIMARY)
                .build();
    }

    @Test
    @DisplayName("updateRequirementStatus - should fulfill both major and liberal requirements when matching")
    void updateUserRequiredCourseStatus_whenCourseIdMatches_thenMarkFulfilled() {
        // given
        Long userId = 1L;
        given(userRequiredCourseStatusRepository.findAllByUserId(userId)).willReturn(List.of(userRequiredCourseStatus1, userRequiredCourseStatus2));
        given(requiredMajorCourseRepository.findById(userRequiredCourseStatus1.getRequiredCourseId())).willReturn(Optional.of(requiredMajorCourse));
        given(requiredMajorCourseRepository.matchesCourseOf(userRequiredCourseStatus1.getRequiredCourseId(), ucc1.getId())).willReturn(true);
        given(requiredLiberalCategoryByCollegeRepository.findById(userRequiredCourseStatus2.getRequiredCourseId())).willReturn(Optional.of(rlc));
        given(userCompletedCourseRepository.sumCreditsByUserAndLiberalCategory(userId, liberalCategory.getId())).willReturn(6);

        // when
        userRequiredCourseStatusService.updateUserRequiredCourseStatus(userId, List.of(ucc1, ucc2));

        // then
        assertThat(userRequiredCourseStatus1.isFulfilled()).isEqualTo(true);
        assertThat(userRequiredCourseStatus2.isFulfilled()).isEqualTo(true);
    }

    @Test
    @DisplayName("updateRequirementStatus - should fulfill major requirement using transfer rule when not directly matched")
    void updateUserRequiredCourseStatus_whenNotDirectlyMatched_thenFulfillViaTransferRule() {
        // given
        Long userId = 1L;
        given(userRequiredCourseStatusRepository.findAllByUserId(userId)).willReturn(List.of(userRequiredCourseStatus1, userRequiredCourseStatus2));
        given(requiredMajorCourseRepository.findById(userRequiredCourseStatus1.getRequiredCourseId())).willReturn(Optional.of(requiredMajorCourse));
        given(courseCreditTransferRuleService.isTransferable(ucc3, requiredMajorCourse)).willReturn(true);
        given(requiredLiberalCategoryByCollegeRepository.findById(userRequiredCourseStatus2.getRequiredCourseId())).willReturn(Optional.of(rlc));
        given(userCompletedCourseRepository.sumCreditsByUserAndLiberalCategory(userId, liberalCategory.getId())).willReturn(6);

        // when
        userRequiredCourseStatusService.updateUserRequiredCourseStatus(userId, List.of(ucc2, ucc3));

        // then
        assertThat(userRequiredCourseStatus1.isFulfilled()).isEqualTo(true);
        assertThat(userRequiredCourseStatus2.isFulfilled()).isEqualTo(true);
    }

    @Test
    @DisplayName("updateRequirementStatus - should throw when user required course status not found")
    void updateUserRequiredCourseStatus_shouldThrow_whenUserRequiredCourseStatusNotFound() {
        // given
        Long userId = 1L;
        given(userRequiredCourseStatusRepository.findAllByUserId(userId)).willReturn(List.of());
        // when & then
        assertThrows(UserRequiredCourseStatusNotFoundException.class,
                () -> userRequiredCourseStatusService.updateUserRequiredCourseStatus(userId, List.of(ucc2, ucc3)));
    }

    @Test
    @DisplayName("updateRequirementStatus - should throw when required major course not found")
    void updateUserRequiredCourseStatus_shouldThrow_whenRequiredMajorCourseNotFound() {
        // given
        Long userId = 1L;
        given(userRequiredCourseStatusRepository.findAllByUserId(userId)).willReturn(List.of(userRequiredCourseStatus1, userRequiredCourseStatus2));
        given(requiredMajorCourseRepository.findById(userRequiredCourseStatus1.getRequiredCourseId())).willReturn(Optional.empty());

        // when & then
        assertThrows(RequiredMajorCourseNotFoundException.class,
                () -> userRequiredCourseStatusService.updateUserRequiredCourseStatus(userId, List.of(ucc2, ucc3)));
    }

    @Test
    @DisplayName("updateRequirementStatus - should throw when user completed course not found")
    void updateUserRequiredCourseStatus_shouldThrow_whenRequiredLiberalCategoryNotFound() {
        // given
        Long userId = 1L;
        given(userRequiredCourseStatusRepository.findAllByUserId(userId)).willReturn(List.of(userRequiredCourseStatus1, userRequiredCourseStatus2));
        given(requiredMajorCourseRepository.findById(userRequiredCourseStatus1.getRequiredCourseId())).willReturn(Optional.of(requiredMajorCourse));
        given(requiredMajorCourseRepository.matchesCourseOf(userRequiredCourseStatus1.getRequiredCourseId(), ucc1.getId())).willReturn(true);
        given(requiredLiberalCategoryByCollegeRepository.findById(userRequiredCourseStatus2.getRequiredCourseId())).willReturn(Optional.empty());
        // when & then
        assertThrows(RequiredLiberalCategoryNotFoundException.class,
                () -> userRequiredCourseStatusService.updateUserRequiredCourseStatus(userId, List.of(ucc2, ucc3)));
    }

    @Test
    @DisplayName("replaceUserRequiredCourseStatus - should throw when user completed course not found")
    void replaceUserRequiredCourseStatus_shouldSucceed() {
        // given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(profileRepository.findByUserId(userId)).willReturn(Optional.of(profile));
        given(userMajorRepository.findAllByUserIdWithMajorAndCollege(userId)).willReturn(List.of(userMajor));
        given(requiredMajorCourseQueryService.getApplicableMajorCourses(1L, MajorType.PRIMARY, 2025)).willReturn(List.of(requiredMajorCourse));
        given(requiredLiberalCategoryQueryService.getApplicableLiberalCategories(1L, 2025)).willReturn(List.of(rlc));

        // when
        userRequiredCourseStatusService.replaceUserRequiredCourseStatus(userId);

        // then
        verify(userRequiredCourseStatusRepository).deleteAllByUserId(userId);
        verify(userRequiredCourseStatusRepository, times(2)).saveAll(anyList());
    }

}
