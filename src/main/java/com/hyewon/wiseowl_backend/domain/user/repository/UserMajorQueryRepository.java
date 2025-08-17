package com.hyewon.wiseowl_backend.domain.user.repository;

import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;
import com.hyewon.wiseowl_backend.domain.user.dto.UserMajorDetail;
import com.hyewon.wiseowl_backend.domain.user.entity.UserMajor;

import java.util.List;
import java.util.Optional;

public interface UserMajorQueryRepository {
    Optional<UserMajorDetail> findUserMajorWithCollege(Long userId, MajorType majorType);
    List<UserMajor> findAllByUserIdWithMajorAndCollege(Long userId);
}
