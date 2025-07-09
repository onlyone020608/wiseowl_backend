package com.hyewon.wiseowl_backend.domain.user.repository;

import com.hyewon.wiseowl_backend.domain.user.entity.UserMajor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserMajorRepository extends JpaRepository<UserMajor, Long> {
    List<UserMajor> findAllByUserId(Long userId);
}
