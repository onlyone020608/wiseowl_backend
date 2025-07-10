package com.hyewon.wiseowl_backend.domain.requirement.repository;

import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;
import com.hyewon.wiseowl_backend.domain.requirement.entity.QRequiredMajorCourse;
import com.hyewon.wiseowl_backend.domain.requirement.entity.RequiredMajorCourse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class RequiredMajorCourseQueryRepositoryImpl implements RequiredMajorCourseQueryRepository {
    private final JPAQueryFactory query;


    @Override
    public List<RequiredMajorCourse> findApplicableMajorCourses(Long majorId, MajorType majorType, Integer entranceYear) {
        QRequiredMajorCourse rmc = QRequiredMajorCourse.requiredMajorCourse;

        return query.select(rmc)
                .from(rmc)
                .where(
                        rmc.major.id.eq(majorId),
                        rmc.majorType.eq(majorType),
                        rmc.appliesFromYear.coalesce(0).loe(entranceYear),
                        rmc.appliesToYear.coalesce(Integer.MAX_VALUE).goe(entranceYear)

                )
                .fetch();
    }
}
