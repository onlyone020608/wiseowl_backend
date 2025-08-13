package com.hyewon.wiseowl_backend.domain.course.repository;

import com.hyewon.wiseowl_backend.domain.course.entity.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CourseOfferingQueryRepositoryImpl implements CourseOfferingQueryRepository {
    private final JPAQueryFactory query;

    @Override
    public List<LiberalCategory> findDistinctLiberalCategoriesBySemester(Long semesterId) {
        QCourseOffering co =  QCourseOffering.courseOffering;
        QCourse c = QCourse.course;
        QLiberalCategoryCourse lcc = QLiberalCategoryCourse.liberalCategoryCourse;
        QLiberalCategory lc = QLiberalCategory.liberalCategory;
        return query
                .selectDistinct(lc)
                .from(co)
                .join(co.course, c)
                .join(lcc).on(lcc.course.eq(c))
                .join(lcc.liberalCategory, lc)
                .where(co.semester.id.eq(semesterId))
                .fetch();
    }
}
