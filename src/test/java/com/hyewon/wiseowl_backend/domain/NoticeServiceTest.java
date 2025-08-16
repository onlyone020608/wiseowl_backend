package com.hyewon.wiseowl_backend.domain;

import com.hyewon.wiseowl_backend.domain.course.entity.Major;
import com.hyewon.wiseowl_backend.domain.course.service.MajorQueryService;
import com.hyewon.wiseowl_backend.domain.notice.dto.NoticeDetailResponse;
import com.hyewon.wiseowl_backend.domain.notice.dto.NoticeResponse;
import com.hyewon.wiseowl_backend.domain.notice.entity.Notice;
import com.hyewon.wiseowl_backend.domain.notice.entity.Organization;
import com.hyewon.wiseowl_backend.domain.notice.repository.NoticeRepository;
import com.hyewon.wiseowl_backend.domain.notice.repository.OrganizationRepository;
import com.hyewon.wiseowl_backend.domain.notice.service.NoticeService;
import com.hyewon.wiseowl_backend.domain.user.entity.SubscriptionType;
import com.hyewon.wiseowl_backend.domain.user.entity.User;
import com.hyewon.wiseowl_backend.domain.user.entity.UserSubscription;
import com.hyewon.wiseowl_backend.domain.user.service.UserSubscriptionService;
import com.hyewon.wiseowl_backend.global.exception.OrganizationNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class NoticeServiceTest {
    @Mock private NoticeRepository noticeRepository;
    @Mock private UserSubscriptionService userSubscriptionService;
    @Mock private MajorQueryService majorQueryService;
    @Mock private OrganizationRepository organizationRepository;
    @InjectMocks private NoticeService noticeService;

    private User user;
    private Major major;
    private Notice notice;
    private Notice notice2;
    private Notice notice3;
    private UserSubscription userSubscription;
    private UserSubscription userSubscription2;
    private Organization organization;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .build();
        major = Major.builder()
                .id(2L)
                .name("컴퓨터공학과")
                .build();
        organization = Organization.builder()
                .id(3L)
                .name("국제교류원")
                .build();
        notice = Notice.builder()
                .title("졸업시험공지사항")
                .postedAt(LocalDate.of(2025, 7, 14))
                .url("www.example.com")
                .sourceId(2L)
                .type(SubscriptionType.MAJOR)
                .build();
        notice2 = Notice.builder()
                .title("졸업시험공지사항2")
                .postedAt(LocalDate.of(2025, 7, 10))
                .url("www.example.com")
                .type(SubscriptionType.MAJOR)
                .sourceId(2L)
                .build();
        notice3 = Notice.builder()
                .title("국제교류원공지사항")
                .postedAt(LocalDate.of(2025, 7, 10))
                .url("www.example.com")
                .sourceId(3L)
                .type(SubscriptionType.ORGANIZATION)
                .build();
        userSubscription = UserSubscription.builder()
                .user(user)
                .targetId(2L)
                .type(SubscriptionType.MAJOR)
                .build();
        userSubscription2 = UserSubscription.builder()
                .user(user)
                .targetId(3L)
                .type(SubscriptionType.ORGANIZATION)
                .build();
    }

    @Test
    @DisplayName("getAllFacilities - should return up to 6 recent notices for each subscribed target")
    void getAllFacilities_success() {
        // given
        Long userId = 1L;
        List<NoticeDetailResponse> responses1 = List.of(NoticeDetailResponse.from(notice), NoticeDetailResponse.from(notice2));
        List<NoticeDetailResponse> responses2 = List.of(NoticeDetailResponse.from(notice3));
        given(userSubscriptionService.getSubscriptions(userId))
                .willReturn(List.of(userSubscription, userSubscription2));
        given(majorQueryService.getMajorName(2L))
                .willReturn("컴퓨터공학과");
        given(organizationRepository.findById(3L)).willReturn(Optional.of(organization));
        given(noticeRepository.findTop6BySourceIdAndTypeOrderByPostedAtDesc(2L, SubscriptionType.MAJOR)).willReturn(responses1);
        given(noticeRepository.findTop6BySourceIdAndTypeOrderByPostedAtDesc(3L, SubscriptionType.ORGANIZATION)).willReturn(responses2);

        // when
        List<NoticeResponse> response = noticeService.getUserSubscribedNotices(userId).stream()
                .sorted(Comparator.comparing(NoticeResponse::subscriptionName))
                .toList();

        // then
        assertThat(response).hasSize(2);
        assertThat(response.get(0).notices()).hasSize(1);
        assertThat(response.get(0).notices().getFirst().title()).isEqualTo("국제교류원공지사항");
        assertThat(response.get(0).subscriptionName()).isEqualTo("국제교류원");
        assertThat(response.get(1).notices()).hasSize(2);
        assertThat(response.get(1).subscriptionName()).isEqualTo("컴퓨터공학과");
        assertThat(response.get(1).notices().getFirst().title()).isEqualTo("졸업시험공지사항");
    }

    @Test
    @DisplayName("getAllFacilities - should throw OrganizationNotFoundException when organization does not exist")
    void getAllFacilities_shouldThrowException_whenOrganizationNotFound() {
        // given
        Long userId = 1L;
        given(userSubscriptionService.getSubscriptions(userId))
                .willReturn(List.of(userSubscription, userSubscription2));
        given(majorQueryService.getMajorName(2L))
                .willReturn("컴퓨터공학과");
        given(organizationRepository.findById(3L)).willReturn(Optional.empty());

        // when & then
        assertThrows(OrganizationNotFoundException.class,
                () ->  noticeService.getUserSubscribedNotices(userId));
    }
}
