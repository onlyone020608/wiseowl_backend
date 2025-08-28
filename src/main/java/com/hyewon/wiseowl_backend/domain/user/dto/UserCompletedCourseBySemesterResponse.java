package com.hyewon.wiseowl_backend.domain.user.dto;

import com.hyewon.wiseowl_backend.domain.course.entity.Term;

import java.util.List;

public record UserCompletedCourseBySemesterResponse(Long semesterId, int year, Term term, List<UserCompletedCourseItem> completedCourses) {
}
