package com.hyewon.wiseowl_backend.domain.course.repository;

import com.hyewon.wiseowl_backend.domain.course.entity.LiberalCategory;
import com.hyewon.wiseowl_backend.domain.course.entity.Major;

import java.util.List;

public interface CourseOfferingQueryRepository {
    List<LiberalCategory> findDistinctLiberalCategoriesBySemester(Long semesterId);
    List<Major> findDistinctMajorsBySemesterId(Long semesterId);
}
