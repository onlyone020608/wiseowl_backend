package com.hyewon.wiseowl_backend.domain.user.repository;

import com.hyewon.wiseowl_backend.domain.user.entity.UserCompletedCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserCompletedCourseRepository extends JpaRepository<UserCompletedCourse, Long>, UserCompletedCourseQueryRepository {
    @Query("""
    SELECT ucc
    FROM UserCompletedCourse ucc
    JOIN FETCH ucc.courseOffering co
    JOIN FETCH co.semester s
    JOIN FETCH co.course c
    WHERE ucc.user.id = :userId
    """)
    List<UserCompletedCourse> findAllByUserIdWithCourseOffering(Long userId);
    List<UserCompletedCourse> findByUserId(Long userId);
    boolean existsByUserIdAndCourseOffering_Id(Long userId, Long courseOfferingId);
}
