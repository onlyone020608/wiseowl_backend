package com.hyewon.wiseowl_backend.domain.user.repository;

import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;
import com.hyewon.wiseowl_backend.domain.user.dto.UserMajorDetail;

import java.util.Optional;

public interface UserMajorQueryRepository {
    Optional<UserMajorDetail> findUserMajorWithCollege(Long userId, MajorType majorType);
}
