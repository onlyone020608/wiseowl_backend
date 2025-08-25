package com.hyewon.wiseowl_backend.domain.user.repository;

import com.hyewon.wiseowl_backend.domain.course.entity.CourseType;
import com.hyewon.wiseowl_backend.domain.course.entity.QCourse;
import com.hyewon.wiseowl_backend.domain.course.entity.QCourseOffering;
import com.hyewon.wiseowl_backend.domain.course.entity.QLiberalCategoryCourse;
import com.hyewon.wiseowl_backend.domain.requirement.entity.QCourseCreditTransferRule;
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
    private final QLiberalCategoryCourse liberalCategoryCourse = QLiberalCategoryCourse.liberalCategoryCourse;

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
    public int sumCreditsByUserAndMajor(Long userId, Long majorId) {
        QCourseCreditTransferRule creditTransferRule = QCourseCreditTransferRule.courseCreditTransferRule;
        Integer sum = query
                .select(c.credit.sum())
                .from(ucc)
                .join(ucc.courseOffering, co)
                .join(co.course, c)
                .join(ucc.user, user)
                .leftJoin(creditTransferRule).on(creditTransferRule.fromCourse.id.eq(c.id))
                .where(
                        user.id.eq(userId),
                        c.major.id.eq(majorId)
                                .or(creditTransferRule.toMajor.id.eq(majorId))
                )
                .fetchOne();

        return sum != null ? sum : 0;
    }

    @Override
    public int sumCreditsByUserAndLiberalCategory(Long userId, Long liberalCategoryId) {
        QCourse courseFromOffering = new QCourse("courseFromOffering");
        QCourse courseFromCategory = new QCourse("courseFromCategory");

        Integer result = query.select(courseFromOffering.credit.sum())
                .from(ucc)
                .join(ucc.courseOffering, co)
                .join(co.course, courseFromOffering)
                .join(liberalCategoryCourse).on(liberalCategoryCourse.liberalCategory.id.eq(liberalCategoryId))
                .join(liberalCategoryCourse.course, courseFromCategory)
                .where(
                        user.id.eq(userId),
                        courseFromOffering.id.eq(courseFromCategory.id),
                        courseFromOffering.courseType.eq(CourseType.GENERAL)
                ).fetchOne();

        return result != null ? result : 0;
    }
}
