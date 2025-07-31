package com.hyewon.wiseowl_backend.domain.requirement.service;

import com.hyewon.wiseowl_backend.domain.requirement.entity.CreditRequirement;
import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;
import com.hyewon.wiseowl_backend.domain.requirement.repository.CreditRequirementRepository;
import com.hyewon.wiseowl_backend.global.exception.CreditRequirementNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CreditRequirementQueryService {
    private final CreditRequirementRepository creditRequirementRepository;

    @Transactional(readOnly = true)
    public List<CreditRequirement> getCreditRequirements(Long majorId, MajorType majorType){
        List<CreditRequirement> results = creditRequirementRepository.findAllByMajorIdAndMajorType(majorId, majorType);
        if(results.isEmpty()){
            throw new CreditRequirementNotFoundException(majorId, majorType);
        }
        return results;
    }
}
