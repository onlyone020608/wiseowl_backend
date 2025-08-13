package com.hyewon.wiseowl_backend.domain.requirement.service;

import com.hyewon.wiseowl_backend.domain.requirement.entity.RequiredLiberalCategoryByCollege;
import com.hyewon.wiseowl_backend.domain.requirement.repository.RequiredLiberalCategoryByCollegeRepository;
import com.hyewon.wiseowl_backend.global.exception.RequiredLiberalCategoryNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RequiredLiberalCategoryQueryService {
    private final RequiredLiberalCategoryByCollegeRepository requiredLiberalCategoryByCollegeRepository;

    @Transactional(readOnly = true)
    public List<RequiredLiberalCategoryByCollege> getApplicableLiberalCategories(Long collegeId, int entranceYear) {
        return requiredLiberalCategoryByCollegeRepository.findApplicableLiberalCategories
                (collegeId, entranceYear);
    }

    @Transactional(readOnly = true)
    public RequiredLiberalCategoryByCollege getRequiredLiberalCategory(Long requiredLiberalCategoryId) {
        return requiredLiberalCategoryByCollegeRepository.findById(requiredLiberalCategoryId).orElseThrow(() -> new RequiredLiberalCategoryNotFoundException(requiredLiberalCategoryId));
    }
}
