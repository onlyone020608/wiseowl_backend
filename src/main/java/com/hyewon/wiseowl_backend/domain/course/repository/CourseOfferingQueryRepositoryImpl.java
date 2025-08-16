package com.hyewon.wiseowl_backend.domain.course.repository;

import com.hyewon.wiseowl_backend.domain.course.entity.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CourseOfferingQueryRepositoryImpl implements CourseOfferingQueryRepository {
    private final JPAQueryFactory query;
    private final QCourseOffering co =  QCourseOffering.courseOffering;
    private final QCourse c = QCourse.course;

    @Override
    public List<LiberalCategory> findDistinctLiberalCategoriesBySemester(Long semesterId) {
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

    @Override
    public List<Major> findDistinctMajorsBySemesterId(Long semesterId) {
        QMajor major =  QMajor.major;
        return query
                .selectDistinct(major)
                .from(co)
                .join(co.course, c)
                .join(c.major, major)
                .where(co.semester.id.eq(semesterId))
                .fetch();
    }
}
