package com.hyewon.wiseowl_backend.domain.user.repository;

import com.hyewon.wiseowl_backend.domain.user.entity.UserCompletedCourse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserCompletedCourseRepository extends JpaRepository<UserCompletedCourse, Long> {
    List<UserCompletedCourse> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
