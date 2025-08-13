package com.hyewon.wiseowl_backend.domain.user.repository;

import com.hyewon.wiseowl_backend.domain.course.entity.CourseType;
import com.hyewon.wiseowl_backend.domain.user.entity.UserRequiredCourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRequiredCourseStatusRepository extends JpaRepository<UserRequiredCourseStatus, Long> {
    List<UserRequiredCourseStatus> findAllByUserId(Long userId);
    List<UserRequiredCourseStatus> findAllByUserIdAndCourseType(Long userId, CourseType courseType);
}
