package com.hyewon.wiseowl_backend.domain.course.repository;

import com.hyewon.wiseowl_backend.domain.course.entity.Major;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.hyewon.wiseowl_backend.domain.course.entity.CourseOffering;

import java.util.List;

public interface CourseOfferingRepository extends JpaRepository<CourseOffering, Long>, CourseOfferingQueryRepository {

    @Query("SELECT DISTINCT co.course.major FROM CourseOffering co WHERE co.semester.id = :semesterId")
    List<Major> findDistinctMajorsBySemesterId(@Param("semesterId") Long semesterId);
    List<CourseOffering> findAllBySemesterId(Long semesterId);
}
