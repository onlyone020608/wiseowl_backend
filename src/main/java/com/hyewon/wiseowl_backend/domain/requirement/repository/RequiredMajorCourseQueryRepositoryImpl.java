package com.hyewon.wiseowl_backend.domain.requirement.repository;

import com.hyewon.wiseowl_backend.domain.course.entity.QCourse;
import com.hyewon.wiseowl_backend.domain.course.entity.QMajor;
import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;
import com.hyewon.wiseowl_backend.domain.requirement.entity.QRequiredMajorCourse;
import com.hyewon.wiseowl_backend.domain.requirement.entity.RequiredMajorCourse;
import com.hyewon.wiseowl_backend.domain.user.entity.QUserCompletedCourse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class RequiredMajorCourseQueryRepositoryImpl implements RequiredMajorCourseQueryRepository {
    private final JPAQueryFactory query;
    private final QRequiredMajorCourse rmc = QRequiredMajorCourse.requiredMajorCourse;
    private final QUserCompletedCourse userCompletedCourse = QUserCompletedCourse.userCompletedCourse;
    private final QCourse course = QCourse.course;

    @Override
    public List<RequiredMajorCourse> findApplicableMajorCourses(Long majorId, MajorType majorType, Integer entranceYear) {
        QMajor major = QMajor.major;

        return query.select(rmc)
                .from(rmc)
                .join(rmc.major, major)
                .where(
                        major.id.eq(majorId),
                        rmc.majorType.eq(majorType),
                        rmc.appliesFromYear.coalesce(0).loe(entranceYear),
                        rmc.appliesToYear.coalesce(Integer.MAX_VALUE).goe(entranceYear)
                )
                .fetch();
    }

    @Override
    public boolean matchesCourseOf(Long requiredCourseId, Long completedCourseId) {
        return query.selectOne()
                .from(rmc)
                .join(rmc.course, course)
                .join(userCompletedCourse).on(userCompletedCourse.courseOffering.course.eq(course))
                .where(
                        rmc.id.eq(requiredCourseId),
                        userCompletedCourse.id.eq(completedCourseId)
                )
                .fetchFirst() != null;
    }
}
