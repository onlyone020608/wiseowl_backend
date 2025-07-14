package com.hyewon.wiseowl_backend.domain.user.repository;

import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;
import com.hyewon.wiseowl_backend.domain.user.entity.UserMajor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserMajorRepository extends JpaRepository<UserMajor, Long> {
    List<UserMajor> findAllByUserId(Long userId);
    UserMajor findByUserIdAndMajorType(Long userId, MajorType majorType);
    Optional<UserMajor> findByUserIdAndMajorTypeIn(Long userId, List<MajorType> majorTypes);
}
