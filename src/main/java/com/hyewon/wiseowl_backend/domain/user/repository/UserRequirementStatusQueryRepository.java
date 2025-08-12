package com.hyewon.wiseowl_backend.domain.user.repository;

import com.hyewon.wiseowl_backend.domain.course.entity.Major;
import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;
import com.hyewon.wiseowl_backend.domain.user.entity.UserRequirementStatus;

import java.util.List;

public interface UserRequirementStatusQueryRepository {
    List<UserRequirementStatus> findByUserAndMajor(Long userId, Major major, MajorType majorType);
}
