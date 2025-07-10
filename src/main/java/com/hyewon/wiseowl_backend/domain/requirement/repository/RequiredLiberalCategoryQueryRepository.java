package com.hyewon.wiseowl_backend.domain.requirement.repository;

import com.hyewon.wiseowl_backend.domain.requirement.entity.RequiredLiberalCategoryByCollege;

import java.util.List;

public interface RequiredLiberalCategoryQueryRepository {
    List<RequiredLiberalCategoryByCollege> findApplicableLiberalCategories(Long collegeId, int entranceYear);
}
