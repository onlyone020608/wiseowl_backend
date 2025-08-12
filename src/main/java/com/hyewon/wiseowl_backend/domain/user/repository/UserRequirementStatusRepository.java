package com.hyewon.wiseowl_backend.domain.user.repository;

import com.hyewon.wiseowl_backend.domain.user.entity.UserRequirementStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRequirementStatusRepository extends JpaRepository<UserRequirementStatus, Long>, UserRequirementStatusQueryRepository {
    List<UserRequirementStatus> findAllByUserId(Long userId);
}
