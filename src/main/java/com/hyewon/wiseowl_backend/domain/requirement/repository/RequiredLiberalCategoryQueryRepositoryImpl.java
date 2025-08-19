package com.hyewon.wiseowl_backend.domain.requirement.repository;

import com.hyewon.wiseowl_backend.domain.course.entity.QMajor;
import com.hyewon.wiseowl_backend.domain.requirement.entity.QRequiredLiberalCategory;
import com.hyewon.wiseowl_backend.domain.requirement.entity.RequiredLiberalCategory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class RequiredLiberalCategoryQueryRepositoryImpl implements RequiredLiberalCategoryQueryRepository {
    private final JPAQueryFactory query;

    @Override
    public List<RequiredLiberalCategory> findApplicableLiberalCategories(Long majorId, int entranceYear) {
        QRequiredLiberalCategory rlc = QRequiredLiberalCategory.requiredLiberalCategory;
        QMajor major  = QMajor.major;

        return query.select(rlc)
                .from(rlc)
                .join(rlc.major, major)
                .where(
                        major.id.eq(majorId),
                        rlc.appliesFromYear.coalesce(0).loe(entranceYear),
                        rlc.appliesToYear.coalesce(Integer.MAX_VALUE).goe(entranceYear)
                )
                .fetch();
    }
}
