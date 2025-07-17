package com.hyewon.wiseowl_backend.domain.user.repository;

import com.hyewon.wiseowl_backend.domain.course.entity.QCourse;
import com.hyewon.wiseowl_backend.domain.course.entity.QCourseOffering;
import com.hyewon.wiseowl_backend.domain.user.dto.CreditAndGradeDto;
import com.hyewon.wiseowl_backend.domain.user.entity.QUserCompletedCourse;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class UserCompletedCourseQueryRepositoryImpl implements UserCompletedCourseQueryRepository {
    private final JPAQueryFactory query;

    @Override
    public List<CreditAndGradeDto> findCourseCreditsAndGradesByUserId(Long userId) {
        QUserCompletedCourse ucc = QUserCompletedCourse.userCompletedCourse;
        QCourseOffering co = QCourseOffering.courseOffering;
        QCourse c = QCourse.course;
        return query
                .select(Projections.constructor(CreditAndGradeDto.class, c.credit, ucc.grade))
                .from(ucc)
                .join(ucc.courseOffering, co)
                .join(co.course, c)
                .where(ucc.user.id.eq(userId))
                .fetch();
    }
}
