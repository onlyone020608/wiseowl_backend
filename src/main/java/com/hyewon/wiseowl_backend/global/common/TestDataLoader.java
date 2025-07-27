package com.hyewon.wiseowl_backend.global.common;

import com.hyewon.wiseowl_backend.domain.auth.entity.RefreshToken;
import com.hyewon.wiseowl_backend.domain.auth.repository.RefreshTokenRepository;
import com.hyewon.wiseowl_backend.domain.auth.security.JwtProvider;
import com.hyewon.wiseowl_backend.domain.course.entity.*;
import com.hyewon.wiseowl_backend.domain.course.repository.*;
import com.hyewon.wiseowl_backend.domain.facility.entity.Facility;
import com.hyewon.wiseowl_backend.domain.facility.repository.FacilityRepository;
import com.hyewon.wiseowl_backend.domain.notice.entity.Notice;
import com.hyewon.wiseowl_backend.domain.notice.entity.Organization;
import com.hyewon.wiseowl_backend.domain.notice.repository.NoticeRepository;
import com.hyewon.wiseowl_backend.domain.notice.repository.OrganizationRepository;
import com.hyewon.wiseowl_backend.domain.requirement.entity.*;
import com.hyewon.wiseowl_backend.domain.requirement.repository.*;
import com.hyewon.wiseowl_backend.domain.user.entity.*;
import com.hyewon.wiseowl_backend.domain.user.repository.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;



@RequiredArgsConstructor
@Component
@Profile("test")
public class TestDataLoader {

    private final UserRepository userRepository;
    private final MajorRepository majorRepository;
    private final OrganizationRepository organizationRepository;
    private final NoticeRepository noticeRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final CourseOfferingRepository courseOfferingRepository;
    private final CourseRepository courseRepository;
    private final SemesterRepository semesterRepository;
    private final LiberalCategoryRepository liberalCategoryRepository;
    private final LiberalCategoryCourseRepository liberalCategoryCourseRepository;
    private final BuildingRepository buildingRepository;
    private final CollegeRepository collegeRepository;
    private final FacilityRepository facilityRepository;
    private final ProfileRepository profileRepository;
    private final UserRequirementStatusRepository userRequirementStatusRepository;
    private final MajorRequirementRepository majorRequirementRepository;
    private final RequirementRepository requirementRepository;
    private final CreditRequirementRepository creditRequirementRepository;
    private final UserMajorRepository userMajorRepository;
    private final UserRequiredCourseStatusRepository userRequiredCourseStatusRepository;
    private final RequiredMajorCourseRepository requiredMajorCourseRepository;
    private final RequiredLiberalCategoryByCollegeRepository requiredLiberalCategoryByCollegeRepository;
    private final UserCompletedCourseRepository userCompletedCourseRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private User testUser;
    private Semester testSemester;
    private String refreshToken;


    @PostConstruct
    public void load() {
        College college = collegeRepository.save(
                College.builder()
                        .name("공과대학")
                        .build()
        );

        Major major = majorRepository.save(Major.builder()
                        .name("컴퓨터공학과")
                        .college(college)
                .build());

        Organization org = organizationRepository.save(
                Organization.builder()
                        .name("국제교류원")
                        .build()

        );

        com.hyewon.wiseowl_backend.domain.user.entity.Profile profile = com.hyewon.wiseowl_backend.domain.user.entity.Profile.builder()
                .GPA(4.1)
                .build();

        testUser =User.builder()
                .email("test@example.com")
                .password(passwordEncoder.encode("encoded-password"))
                .username("Tester")
                        .profile(profile)
                .studentId("2021")
                .build();

        refreshToken = jwtProvider.generateRefreshToken(testUser.getEmail());
        refreshTokenRepository.save(RefreshToken.builder()
                        .email(testUser.getEmail())
                        .token(refreshToken)
                .build());

        profile.assignUser(testUser);

        userRepository.save(testUser);

        userSubscriptionRepository.saveAll(List.of(
                UserSubscription.builder()
                        .user(testUser)
                        .targetId(major.getId())
                        .type(SubscriptionType.MAJOR)
                        .build(),
        UserSubscription.builder()
                .user(testUser)
                .targetId(org.getId())
                .type(SubscriptionType.ORGANIZATION)
                .build()
        ));

        noticeRepository.saveAll(List.of(
                Notice.builder()
                        .title("졸업시험공지사항")
                        .postedAt(LocalDate.of(2025, 7, 14))
                        .url("www.example.com")
                        .sourceId(2L)
                        .build(),
                Notice.builder()
                        .title("졸업시험공지사항2")
                        .postedAt(LocalDate.of(2025, 7, 10))
                        .url("www.example.com")
                        .sourceId(2L)
                        .build()
        ));

        Course liberalCourse = courseRepository.save(
                Course.builder()
                        .name("사회문명")
                        .courseCodePrefix("CD234")
                        .build()
        );

        LiberalCategory liberalCategory = liberalCategoryRepository.save(
                LiberalCategory.builder()
                        .name("인간과사회")
                        .build()
        );

        liberalCategoryCourseRepository.save(
                LiberalCategoryCourse.builder()
                        .liberalCategory(liberalCategory)
                        .course(liberalCourse)
                        .build()
        );

        Course majorCourse = courseRepository.save(
                Course.builder()
                        .major(major)
                        .name("자료구조")
                        .courseCodePrefix("CE153")
                        .build());


        testSemester = semesterRepository.save(Semester.builder()
                .year(2023)
                .build());

        Building building1 = buildingRepository.save(Building.builder()
                        .name("백년관")
                .build());

        Building building2 = buildingRepository.save(Building.builder()
                .name("공학관")
                .build());

        facilityRepository.saveAll(List.of(Facility.builder()
                        .name("편의점")
                        .building(building1)
                        .build()
                , Facility.builder()
                        .name("복사실")
                        .building(building1)
                        .build())
        );

        facilityRepository.saveAll(List.of(Facility.builder()
                        .name("편의점")
                        .building(building2)
                        .build()
                , Facility.builder()
                        .name("열람실")
                        .building(building2)
                        .build())
        );


        courseOfferingRepository.saveAll(List.of(
                CourseOffering.builder()
                        .course(majorCourse)
                        .semester(testSemester)
                        .room("0409")
                        .professor("홍길동")
                        .classTime("화목123")
                        .build(),
                CourseOffering.builder()
                        .course(liberalCourse)
                        .semester(testSemester)
                        .room("0409")
                        .professor("홍길동")
                        .classTime("월금23")
                        .build()
        ));


        Requirement requirement = requirementRepository.save(
                Requirement.builder()
                        .name("졸업시험")
                        .build()
        );

        userMajorRepository.save(UserMajor.builder()
                        .user(testUser)
                .majorType(MajorType.PRIMARY)
                        .major(major)
                .build());

        MajorRequirement majorReq = majorRequirementRepository.save(
                MajorRequirement.builder()
                        .major(major)
                        .majorType(MajorType.PRIMARY)
                        .requirement(requirement)
                        .description("레포트대체가능")
                        .build()
        );

        userRequirementStatusRepository.saveAll(List.of(
                UserRequirementStatus.builder()
                        .user(testUser)
                        .majorRequirement(majorReq)
                        .fulfilled(false)
                        .build()
        ));

        creditRequirementRepository.save(
                CreditRequirement.builder()
                        .major(major)
                        .courseType(CourseType.MAJOR)
                        .majorType(MajorType.PRIMARY)
                        .requiredCredits(130)
                        .build()
        );

        RequiredMajorCourse requiredMajorCourse = requiredMajorCourseRepository.save(
                RequiredMajorCourse.builder()
                        .major(major)
                        .course(majorCourse)
                        .majorType(MajorType.PRIMARY)
                        .build()
        );

        RequiredLiberalCategoryByCollege liberalCategoryByCollege = requiredLiberalCategoryByCollegeRepository.save(
                RequiredLiberalCategoryByCollege.builder()
                        .college(college)
                        .requiredCredit(3)
                        .liberalCategory(liberalCategory)
                        .build()
        );

        userRequiredCourseStatusRepository.saveAll(List.of(
                UserRequiredCourseStatus.builder()
                        .user(testUser)
                        .courseType(CourseType.MAJOR)
                        .requiredCourseId(requiredMajorCourse.getId())
                        .build()
                ,
                UserRequiredCourseStatus.builder()
                        .user(testUser)
                        .courseType(CourseType.GENERAL)
                        .requiredCourseId(liberalCategoryByCollege.getId())
                        .build()
        ));

        userCompletedCourseRepository.saveAll(List.of(
                UserCompletedCourse.builder()
                        .grade(Grade.B_PLUS)
                        .retake(false)
                        .build()
        ));




    }

    public User getTestUser() {
        return testUser;
    }

    public Semester getTestSemester() {
        return testSemester;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
