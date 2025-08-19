package com.hyewon.wiseowl_backend.domain.requirement.repository;

import com.hyewon.wiseowl_backend.domain.requirement.entity.RequiredLiberalCategory;

import java.util.List;

public interface RequiredLiberalCategoryQueryRepository {
    List<RequiredLiberalCategory> findApplicableLiberalCategories(Long majorId, int entranceYear);
}
