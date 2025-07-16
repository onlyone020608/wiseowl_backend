package com.hyewon.wiseowl_backend.domain.notice.dto;

import com.hyewon.wiseowl_backend.domain.notice.entity.Notice;

import java.time.LocalDate;

public record NoticeDetailResponse(
        String title,
        String url,
        LocalDate postedAt

) {
    public static NoticeDetailResponse from(Notice notice) {
        return new NoticeDetailResponse(
                notice.getTitle(),
                notice.getUrl(),
                notice.getPostedAt()
        );
    }
}
