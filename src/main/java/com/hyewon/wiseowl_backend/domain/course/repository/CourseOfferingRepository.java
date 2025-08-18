package com.hyewon.wiseowl_backend.domain.course.repository;

import com.hyewon.wiseowl_backend.domain.course.entity.CourseOffering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseOfferingRepository extends JpaRepository<CourseOffering, Long>, CourseOfferingQueryRepository {
    @Query("SELECT co FROM CourseOffering co " +
            "JOIN FETCH co.course c " +
            "LEFT JOIN FETCH c.major " +
            "WHERE co.semester.id = :semesterId")
    List<CourseOffering> findAllWithCourseAndMajorBySemesterId(@Param("semesterId") Long semesterId);
}
