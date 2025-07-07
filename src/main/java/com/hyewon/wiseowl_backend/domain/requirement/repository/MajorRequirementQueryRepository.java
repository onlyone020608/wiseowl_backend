package com.hyewon.wiseowl_backend.domain.requirement.repository;

import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorRequirement;
import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;

import java.util.List;

public interface MajorRequirementQueryRepository {
    public List<MajorRequirement> findApplicable(Long majorId, MajorType majorType, Integer entranceYear);
}
