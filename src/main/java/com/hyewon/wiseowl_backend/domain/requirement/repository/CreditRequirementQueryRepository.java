package com.hyewon.wiseowl_backend.domain.requirement.repository;

import com.hyewon.wiseowl_backend.domain.course.entity.Major;
import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;
import com.hyewon.wiseowl_backend.domain.requirement.entity.Track;

public interface CreditRequirementQueryRepository {
    int sumRequiredCredits(Major major, MajorType majorType, Track track, Integer entranceYear);
}
