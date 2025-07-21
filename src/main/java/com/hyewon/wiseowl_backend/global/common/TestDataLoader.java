package com.hyewon.wiseowl_backend.global.common;

import com.hyewon.wiseowl_backend.domain.course.entity.*;
import com.hyewon.wiseowl_backend.domain.course.repository.*;
import com.hyewon.wiseowl_backend.domain.notice.entity.Notice;
import com.hyewon.wiseowl_backend.domain.notice.entity.Organization;
import com.hyewon.wiseowl_backend.domain.notice.repository.NoticeRepository;
import com.hyewon.wiseowl_backend.domain.notice.repository.OrganizationRepository;
import com.hyewon.wiseowl_backend.domain.user.entity.SubscriptionType;
import com.hyewon.wiseowl_backend.domain.user.entity.User;
import com.hyewon.wiseowl_backend.domain.user.entity.UserSubscription;
import com.hyewon.wiseowl_backend.domain.user.repository.UserRepository;
import com.hyewon.wiseowl_backend.domain.user.repository.UserSubscriptionRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
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
    private final RoomRepository roomRepository;
    private final BuildingRepository buildingRepository;
    private final CollegeRepository collegeRepository;
    private User testUser;
    private Semester testSemester;


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

        testUser = userRepository.save(User.builder()
                .email("test@example.com")
                .password("encoded-password")
                .username("Tester")
                .build());

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

        Building building = buildingRepository.save(Building.builder()
                        .name("백년관")
                .build());

        Room room = roomRepository.save(Room.builder()
                        .roomNumber("301")
                        .building(building)
                .build());


        courseOfferingRepository.saveAll(List.of(
                CourseOffering.builder()
                        .course(majorCourse)
                        .semester(testSemester)
                        .room(room)
                        .professor("홍길동")
                        .classTime("화목123")
                        .build(),
                CourseOffering.builder()
                        .course(liberalCourse)
                        .semester(testSemester)
                        .room(room)
                        .professor("홍길동")
                        .classTime("월금23")
                        .build()
        ));

    }

    public User getTestUser() {
        return testUser;
    }

    public Semester getTestSemester() {
        return testSemester;
    }
}
