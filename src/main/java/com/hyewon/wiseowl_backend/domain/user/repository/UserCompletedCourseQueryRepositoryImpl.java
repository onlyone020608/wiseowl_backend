package com.hyewon.wiseowl_backend.domain.user.repository;

import com.hyewon.wiseowl_backend.domain.course.entity.QCourse;
import com.hyewon.wiseowl_backend.domain.course.entity.QCourseOffering;
import com.hyewon.wiseowl_backend.domain.user.dto.CreditAndGradeDto;
import com.hyewon.wiseowl_backend.domain.user.entity.QUser;
import com.hyewon.wiseowl_backend.domain.user.entity.QUserCompletedCourse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class UserCompletedCourseQueryRepositoryImpl implements UserCompletedCourseQueryRepository {
    private final JPAQueryFactory query;
    private final QUserCompletedCourse ucc = QUserCompletedCourse.userCompletedCourse;
    private final QCourseOffering co = QCourseOffering.courseOffering;
    private final QCourse c = QCourse.course;
    private final QUser user = QUser.user;

    @Override
    public List<CreditAndGradeDto> findCourseCreditsAndGradesByUserId(Long userId) {
        return query
                .select(Projections.constructor(CreditAndGradeDto.class, c.credit, ucc.grade))
                .from(ucc)
                .join(ucc.courseOffering, co)
                .join(co.course, c)
                .join(ucc.user, user)
                .where(user.id.eq(userId))
                .fetch();
    }

    @Override
    public int sumCreditsByUser(Long userId) {
        Integer sum = query
                .select(c.credit.sum())
                .from(ucc)
                .join(ucc.courseOffering, co)
                .join(co.course, c)
                .join(ucc.user, user)
                .where(user.id.eq(userId))
                .fetchOne();

        return sum != null ? 0 : sum;
    }
}
