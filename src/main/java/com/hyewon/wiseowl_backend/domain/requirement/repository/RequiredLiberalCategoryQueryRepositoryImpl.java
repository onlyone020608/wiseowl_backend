package com.hyewon.wiseowl_backend.domain.requirement.repository;

import com.hyewon.wiseowl_backend.domain.course.entity.QCollege;
import com.hyewon.wiseowl_backend.domain.requirement.entity.QRequiredLiberalCategoryByCollege;
import com.hyewon.wiseowl_backend.domain.requirement.entity.RequiredLiberalCategoryByCollege;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class RequiredLiberalCategoryQueryRepositoryImpl implements RequiredLiberalCategoryQueryRepository {
    private final JPAQueryFactory query;

    @Override
    public List<RequiredLiberalCategoryByCollege> findApplicableLiberalCategories(Long collegeId, int entranceYear) {
        QRequiredLiberalCategoryByCollege rlc = QRequiredLiberalCategoryByCollege.requiredLiberalCategoryByCollege;
        QCollege college = QCollege.college;

        return query.select(rlc)
                .from(rlc)
                .join(rlc.college, college)
                .where(
                        college.id.eq(collegeId),
                        rlc.appliesFromYear.coalesce(0).loe(entranceYear),
                        rlc.appliesToYear.coalesce(Integer.MAX_VALUE).goe(entranceYear)
                )
                .fetch();
    }
}
