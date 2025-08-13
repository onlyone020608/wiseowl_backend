package com.hyewon.wiseowl_backend.domain.requirement.repository;

import com.hyewon.wiseowl_backend.domain.requirement.entity.CourseCreditTransferRule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseCreditTransferRuleRepository extends JpaRepository<CourseCreditTransferRule, Long>, CourseCreditTransferRuleQueryRepository {
}
