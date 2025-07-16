package com.hyewon.wiseowl_backend.domain.notice.repository;

import com.hyewon.wiseowl_backend.domain.notice.entity.Notice;
import com.hyewon.wiseowl_backend.domain.notice.entity.QNotice;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class NoticeQueryRepositoryImpl implements NoticeQueryRepository {
    private final JPAQueryFactory query;


    @Override
    public List<Notice> findTop6BySourceIdOrderByPostedAtDesc(Long sourceId) {
        QNotice notice = QNotice.notice;
        return query.selectFrom(notice)
                .where(notice.sourceId.eq(sourceId))
                .orderBy(notice.postedAt.desc())
                .limit(6)
                .fetch();
    }
}
