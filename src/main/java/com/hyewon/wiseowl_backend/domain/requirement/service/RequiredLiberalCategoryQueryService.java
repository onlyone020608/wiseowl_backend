package com.hyewon.wiseowl_backend.domain.requirement.service;

import com.hyewon.wiseowl_backend.domain.requirement.entity.RequiredLiberalCategory;
import com.hyewon.wiseowl_backend.domain.requirement.repository.RequiredLiberalCategoryRepository;
import com.hyewon.wiseowl_backend.global.exception.RequiredLiberalCategoryNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RequiredLiberalCategoryQueryService {
    private final RequiredLiberalCategoryRepository requiredLiberalCategoryRepository;

    @Transactional(readOnly = true)
    public List<RequiredLiberalCategory> getApplicableLiberalCategories(Long majorId, int entranceYear) {
        return requiredLiberalCategoryRepository.findApplicableLiberalCategories
                (majorId, entranceYear);
    }

    @Transactional(readOnly = true)
    public RequiredLiberalCategory getRequiredLiberalCategory(Long requiredLiberalCategoryId) {
        return requiredLiberalCategoryRepository.findById(requiredLiberalCategoryId).orElseThrow(() -> new RequiredLiberalCategoryNotFoundException(requiredLiberalCategoryId));
    }

    @Transactional(readOnly = true)
    public RequiredLiberalCategory getRequiredLiberalWithCategory(Long requiredLiberalCategoryId) {
        return requiredLiberalCategoryRepository.findByIdWithLiberalCategory(requiredLiberalCategoryId).orElseThrow(() -> new RequiredLiberalCategoryNotFoundException(requiredLiberalCategoryId));
    }
}
