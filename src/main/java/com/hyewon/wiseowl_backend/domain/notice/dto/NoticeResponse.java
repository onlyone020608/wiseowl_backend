package com.hyewon.wiseowl_backend.domain.notice.dto;

import java.util.List;

public record NoticeResponse(
    String subscriptionName,
    List<NoticeDetailResponse> notices
) {
}
