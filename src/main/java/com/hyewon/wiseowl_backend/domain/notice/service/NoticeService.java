package com.hyewon.wiseowl_backend.domain.notice.service;

import com.hyewon.wiseowl_backend.domain.course.service.MajorQueryService;
import com.hyewon.wiseowl_backend.domain.notice.dto.NoticeDetailResponse;
import com.hyewon.wiseowl_backend.domain.notice.dto.NoticeResponse;
import com.hyewon.wiseowl_backend.domain.notice.entity.Notice;
import com.hyewon.wiseowl_backend.domain.notice.entity.Organization;
import com.hyewon.wiseowl_backend.domain.notice.repository.NoticeRepository;
import com.hyewon.wiseowl_backend.domain.notice.repository.OrganizationRepository;
import com.hyewon.wiseowl_backend.domain.user.entity.SubscriptionType;
import com.hyewon.wiseowl_backend.domain.user.entity.UserSubscription;
import com.hyewon.wiseowl_backend.domain.user.service.UserSubscriptionService;
import com.hyewon.wiseowl_backend.global.exception.OrganizationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final UserSubscriptionService userSubscriptionService;
    private final MajorQueryService majorQueryService;
    private final OrganizationRepository organizationRepository;

    @Transactional(readOnly = true)
    public List<NoticeResponse> getUserSubscribedNotices(Long userId) {
        List<UserSubscription> subscriptions = userSubscriptionService.getSubscriptions(userId);
        return subscriptions.stream().map(
                subscription -> {
                    String subscriptionName = null;
                    if (subscription.getType().equals(SubscriptionType.MAJOR)) {
                        subscriptionName = majorQueryService.getMajorName(subscription.getTargetId());

                    }
                    if (subscription.getType().equals(SubscriptionType.ORGANIZATION)) {
                        Organization organization = organizationRepository.findById(subscription.getTargetId()).orElseThrow(
                                () -> new OrganizationNotFoundException(subscription.getTargetId()));
                        subscriptionName = organization.getName();
                    }
                    List<Notice> notices = noticeRepository.findTop6BySourceIdAndTypeOrderByPostedAtDesc(subscription.getTargetId(), subscription.getType());
                    List<NoticeDetailResponse> noticeDetailResponses = notices.stream().map(NoticeDetailResponse::from).toList();

                    return new NoticeResponse(
                            subscriptionName,
                            noticeDetailResponses
                    );
                }
        ).toList();
    }
}
