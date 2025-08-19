package com.hyewon.wiseowl_backend.domain.requirement.repository;

import com.hyewon.wiseowl_backend.domain.requirement.entity.LanguageTestRequirement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageTestRequirementRepository extends JpaRepository<LanguageTestRequirement, Long> {
}
