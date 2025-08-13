package com.hyewon.wiseowl_backend.domain.course.repository;

import com.hyewon.wiseowl_backend.domain.course.entity.LiberalCategoryCourse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LiberalCategoryCourseRepository extends JpaRepository<LiberalCategoryCourse, Long> {
    boolean existsByCourseIdAndLiberalCategoryId(Long courseId, Long liberalCategoryId);
}
