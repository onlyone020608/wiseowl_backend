package com.hyewon.wiseowl_backend.domain.notice.repository;

import com.hyewon.wiseowl_backend.domain.notice.entity.Notice;

import java.util.List;

public interface NoticeQueryRepository {
    List<Notice> findTop6BySourceIdOrderByPostedAtDesc(Long sourceId);
}
