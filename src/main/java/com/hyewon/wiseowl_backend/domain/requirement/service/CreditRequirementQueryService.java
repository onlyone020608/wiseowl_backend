package com.hyewon.wiseowl_backend.domain.requirement.service;

import com.hyewon.wiseowl_backend.domain.course.entity.Major;
import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;
import com.hyewon.wiseowl_backend.domain.requirement.entity.Track;
import com.hyewon.wiseowl_backend.domain.requirement.repository.CreditRequirementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreditRequirementQueryService {
    private final CreditRequirementRepository creditRequirementRepository;

    @Transactional(readOnly = true)
    public int sumRequiredCredits(Major major, MajorType majorType, Track track, Integer entranceYear) {
        return creditRequirementRepository.sumRequiredCredits(major, majorType, track, entranceYear);
    }
}
