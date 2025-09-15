package com.hyewon.wiseowl_backend.domain.course.repository;

import com.hyewon.wiseowl_backend.domain.course.entity.CourseOffering;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseOfferingRepository extends JpaRepository<CourseOffering, Long>, CourseOfferingQueryRepository {
}
