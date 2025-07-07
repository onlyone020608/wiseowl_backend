package com.hyewon.wiseowl_backend.domain.requirement.repository;

import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorRequirement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MajorRequirementRepository extends JpaRepository<MajorRequirement, Long>, MajorRequirementQueryRepository {


}
