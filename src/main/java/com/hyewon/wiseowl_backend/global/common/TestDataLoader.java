package com.hyewon.wiseowl_backend.global.common;

import com.hyewon.wiseowl_backend.domain.auth.entity.RefreshToken;
import com.hyewon.wiseowl_backend.domain.auth.repository.RefreshTokenRepository;
import com.hyewon.wiseowl_backend.domain.auth.security.JwtProvider;
import com.hyewon.wiseowl_backend.domain.course.entity.CourseType;
import com.hyewon.wiseowl_backend.domain.course.entity.Major;
import com.hyewon.wiseowl_backend.domain.course.repository.MajorRepository;
import com.hyewon.wiseowl_backend.domain.notice.entity.Notice;
import com.hyewon.wiseowl_backend.domain.notice.repository.NoticeRepository;
import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorRequirement;
import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;
import com.hyewon.wiseowl_backend.domain.requirement.entity.Requirement;
import com.hyewon.wiseowl_backend.domain.requirement.entity.Track;
import com.hyewon.wiseowl_backend.domain.requirement.repository.MajorRequirementRepository;
import com.hyewon.wiseowl_backend.domain.requirement.repository.RequirementRepository;
import com.hyewon.wiseowl_backend.domain.user.entity.*;
import com.hyewon.wiseowl_backend.domain.user.repository.*;
import com.hyewon.wiseowl_backend.global.exception.MajorNotFoundException;
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
    private final NoticeRepository noticeRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final UserRequirementStatusRepository userRequirementStatusRepository;
    private final MajorRequirementRepository majorRequirementRepository;
    private final RequirementRepository requirementRepository;
    private final UserMajorRepository userMajorRepository;
    private final UserRequiredCourseStatusRepository userRequiredCourseStatusRepository;
    private final UserCompletedCourseRepository userCompletedCourseRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserTrackRepository userTrackRepository;

    private User testUser;
    private String refreshToken;

    @PostConstruct
    public void load() {
        com.hyewon.wiseowl_backend.domain.user.entity.Profile profile = com.hyewon.wiseowl_backend.domain.user.entity.Profile.builder()
                .gpa(4.1)
                .entranceYear(2024)
                .build();
        testUser =User.builder()
                .email("test@example.com")
                .password(passwordEncoder.encode("encoded-password"))
                .username("Tester")
                        .profile(profile)
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
                        .targetId(1L)
                        .type(SubscriptionType.MAJOR)
                        .build(),
                UserSubscription.builder()
                        .user(testUser)
                        .targetId(2L)
                        .type(SubscriptionType.ORGANIZATION)
                        .build()
        ));
        userTrackRepository.save(UserTrack.builder()
                        .user(testUser)
                        .track(Track.PRIMARY_WITH_DOUBLE)
                .build());

        noticeRepository.saveAll(List.of(
                Notice.builder()
                        .title("졸업시험공지사항")
                        .postedAt(LocalDate.of(2025, 7, 14))
                        .url("www.example.com")
                        .sourceId(1L)
                        .build(),
                Notice.builder()
                        .title("졸업시험공지사항2")
                        .postedAt(LocalDate.of(2025, 7, 10))
                        .url("www.example.com")
                        .sourceId(2L)
                        .build()
        ));

        Major major = majorRepository.findById(1L).orElseThrow(() -> new MajorNotFoundException(1L));
        userMajorRepository.save(UserMajor.builder()
                        .user(testUser)
                .majorType(MajorType.PRIMARY)
                        .major(major)
                .build());
        Requirement requirement = requirementRepository.findById(1L).orElse(null);
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
        userRequiredCourseStatusRepository.saveAll(List.of(
                UserRequiredCourseStatus.builder()
                        .user(testUser)
                        .courseType(CourseType.MAJOR)
                        .requiredCourseId(1L)
                        .build()
                ,
                UserRequiredCourseStatus.builder()
                        .user(testUser)
                        .courseType(CourseType.GENERAL)
                        .requiredCourseId(2L)
                        .build()
        ));
        userCompletedCourseRepository.saveAll(List.of(
                UserCompletedCourse.builder()
                        .user(testUser)
                        .grade(Grade.B_PLUS)
                        .retake(false)
                        .build()
        ));
    }

    public User getTestUser() {
        return testUser;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
