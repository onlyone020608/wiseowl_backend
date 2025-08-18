package com.hyewon.wiseowl_backend.domain.requirement.repository;

import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;
import com.hyewon.wiseowl_backend.domain.requirement.entity.RequiredMajorCourse;

import java.util.List;

public interface RequiredMajorCourseQueryRepository {
    List<RequiredMajorCourse> findApplicableMajorCourses(Long majorId, MajorType majorType, Integer entranceYear);
    boolean matchesCourseOf(Long requiredCourseId, Long completedCourseId);
}
