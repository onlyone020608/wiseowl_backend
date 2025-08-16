package com.hyewon.wiseowl_backend.domain.notice.repository;

import com.hyewon.wiseowl_backend.domain.notice.dto.NoticeDetailResponse;
import com.hyewon.wiseowl_backend.domain.user.entity.SubscriptionType;

import java.util.List;

public interface NoticeQueryRepository {
    List<NoticeDetailResponse> findTop6BySourceIdAndTypeOrderByPostedAtDesc(Long sourceId, SubscriptionType type);
}
