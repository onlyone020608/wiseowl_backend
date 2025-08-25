package com.hyewon.wiseowl_backend.domain.notice.repository;

import com.hyewon.wiseowl_backend.domain.notice.dto.NoticeDetailResponse;
import com.hyewon.wiseowl_backend.domain.notice.entity.QNotice;
import com.hyewon.wiseowl_backend.domain.user.entity.SubscriptionType;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class NoticeQueryRepositoryImpl implements NoticeQueryRepository {
    private final JPAQueryFactory query;

    @Override
    public List<NoticeDetailResponse> findTop6BySourceIdAndTypeOrderByPostedAtDesc(Long sourceId, SubscriptionType type) {
        QNotice notice = QNotice.notice;
        return query.select(Projections.constructor(NoticeDetailResponse.class, notice.id, notice.title, notice.url, notice.postedAt))
                .from(notice)
                .where(
                        notice.sourceId.eq(sourceId),
                        notice.type.eq(type)
                        )
                .orderBy(notice.postedAt.desc())
                .limit(6)
                .fetch();
    }
}
