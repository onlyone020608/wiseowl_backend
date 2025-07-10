package com.hyewon.wiseowl_backend.domain.user.repository;

import com.hyewon.wiseowl_backend.domain.user.entity.UserRequiredCourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRequiredCourseStatusRepository extends JpaRepository<UserRequiredCourseStatus, Long> {
}
