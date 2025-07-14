package com.hyewon.wiseowl_backend.domain.requirement.repository;

import com.hyewon.wiseowl_backend.domain.requirement.entity.QCourseCreditTransferRule;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CourseCreditTransferRuleQueryRepositoryImpl implements CourseCreditTransferRuleQueryRepository{
    private final JPAQueryFactory query;

    @Override
    public boolean isCourseTransferable(Long fromCourseId, Long toCourseId, Integer entryYear) {
        QCourseCreditTransferRule cctr = QCourseCreditTransferRule.courseCreditTransferRule;
        return query.select(cctr)
                .from(cctr)
                .where(
                        cctr.fromCourse.id.eq(fromCourseId),
                        cctr.toCourse.id.eq(toCourseId),
                        cctr.entryYearFrom.coalesce(0).loe(entryYear),
                        cctr.entryYearTo.coalesce(Integer.MAX_VALUE).goe(entryYear)

                )
                .fetchFirst() != null;
    }
}
