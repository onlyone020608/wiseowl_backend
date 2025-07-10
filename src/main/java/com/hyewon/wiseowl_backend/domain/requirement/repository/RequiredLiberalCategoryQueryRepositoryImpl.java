package com.hyewon.wiseowl_backend.domain.requirement.repository;

import com.hyewon.wiseowl_backend.domain.requirement.entity.QRequiredLiberalCategoryByCollege;
import com.hyewon.wiseowl_backend.domain.requirement.entity.RequiredLiberalCategoryByCollege;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class RequiredLiberalCategoryQueryRepositoryImpl implements RequiredLiberalCategoryQueryRepository{
    private final JPAQueryFactory query;

    @Override
    public List<RequiredLiberalCategoryByCollege> findApplicableLiberalCategories(Long collegeId, int entranceYear) {
        QRequiredLiberalCategoryByCollege rlc = QRequiredLiberalCategoryByCollege.requiredLiberalCategoryByCollege;
        return query.select(rlc)
                .from(rlc)
                .where(
                        rlc.college.id.eq(collegeId),
                        rlc.appliesFromYear.coalesce(0).loe(entranceYear),
                        rlc.appliesToYear.coalesce(Integer.MAX_VALUE).goe(entranceYear)
                )
                .fetch();
    }
}
