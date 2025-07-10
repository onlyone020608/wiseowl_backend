package com.hyewon.wiseowl_backend.domain.requirement.repository;

import com.hyewon.wiseowl_backend.domain.requirement.entity.CreditRequirement;
import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CreditRequirementRepository extends JpaRepository<CreditRequirement, Integer> {
    List<CreditRequirement> findAllByMajorIdAndMajorType(Long majorId, MajorType majorType);

}
