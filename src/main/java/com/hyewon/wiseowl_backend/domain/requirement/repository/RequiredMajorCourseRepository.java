package com.hyewon.wiseowl_backend.domain.requirement.repository;

import com.hyewon.wiseowl_backend.domain.requirement.entity.RequiredMajorCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RequiredMajorCourseRepository extends JpaRepository<RequiredMajorCourse, Long>, RequiredMajorCourseQueryRepository {
    @Query("select r from RequiredMajorCourse r join fetch r.course where r.id = :id")
    Optional<RequiredMajorCourse> findByIdWithCourse(@Param("id") Long id);
}
