package com.hyewon.wiseowl_backend.domain.user.repository;

import com.hyewon.wiseowl_backend.domain.user.entity.UserRequiredCourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRequiredCourseStatusRepository extends JpaRepository<UserRequiredCourseStatus, Long>, UserRequiredCourseStatusQueryRepository {
    List<UserRequiredCourseStatus> findAllByUserId(Long userId);
    void deleteAllByUserId(Long userId);
}
