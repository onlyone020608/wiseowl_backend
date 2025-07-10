package com.hyewon.wiseowl_backend.domain.requirement.repository;

import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;
import com.hyewon.wiseowl_backend.domain.requirement.entity.RequiredMajorCourse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequiredMajorCourseRepository extends JpaRepository<RequiredMajorCourse, Long>, RequiredMajorCourseQueryRepository {

}
