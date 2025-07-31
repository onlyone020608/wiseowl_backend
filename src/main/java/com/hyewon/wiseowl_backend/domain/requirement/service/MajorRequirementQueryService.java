package com.hyewon.wiseowl_backend.domain.requirement.service;
import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorRequirement;
import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;
import com.hyewon.wiseowl_backend.domain.requirement.repository.MajorRequirementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MajorRequirementQueryService {
    private final MajorRequirementRepository majorRequirementRepository;

    @Transactional(readOnly = true)
    public List<MajorRequirement> getApplicableRequirements(Long majorId, MajorType type, int entranceYear){
        return majorRequirementRepository.findApplicable(majorId,
                type, entranceYear);
    }


}
