package com.hyewon.wiseowl_backend.domain.notice.dto;

import com.hyewon.wiseowl_backend.domain.notice.entity.Notice;

import java.time.LocalDate;

public record NoticeDetailResponse(
        Long noticeId,
        String title,
        String url,
        LocalDate postedAt
) {
    public static NoticeDetailResponse from(Notice notice) {
        return new NoticeDetailResponse(
                notice.getId(),
                notice.getTitle(),
                notice.getUrl(),
                notice.getPostedAt()
        );
    }
}
