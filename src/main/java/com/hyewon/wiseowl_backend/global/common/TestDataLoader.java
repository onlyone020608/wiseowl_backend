package com.hyewon.wiseowl_backend.global.common;

import com.hyewon.wiseowl_backend.domain.course.entity.Major;
import com.hyewon.wiseowl_backend.domain.course.repository.MajorRepository;
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
    private User testUser;


    @PostConstruct
    public void load() {
        Major major = majorRepository.save(Major.builder()
                        .name("컴퓨터공학과")
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

    }

    public User getTestUser() {
        return testUser;
    }
}
