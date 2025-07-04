package com.hyewon.wiseowl_backend.domain.course.repository;

import com.hyewon.wiseowl_backend.domain.course.entity.Course;
import com.hyewon.wiseowl_backend.domain.course.entity.LiberalCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LiberalCategoryRepository extends JpaRepository<LiberalCategory, Long> {
    @Query("SELECT lcc.liberalCategory FROM LiberalCategoryCourse lcc WHERE lcc.course = :course")
    Optional<LiberalCategory> findByCourse(@Param("course") Course course);
}
