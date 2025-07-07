package com.hyewon.wiseowl_backend.domain.user.repository;

import com.hyewon.wiseowl_backend.domain.user.entity.UserRequirementStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRequirementStatusRepository extends JpaRepository<UserRequirementStatus, Long> {
}
