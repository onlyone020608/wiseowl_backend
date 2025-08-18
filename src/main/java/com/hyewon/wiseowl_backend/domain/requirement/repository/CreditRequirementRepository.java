package com.hyewon.wiseowl_backend.domain.requirement.repository;

import com.hyewon.wiseowl_backend.domain.requirement.entity.CreditRequirement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditRequirementRepository extends JpaRepository<CreditRequirement, Integer>, CreditRequirementQueryRepository {
}
