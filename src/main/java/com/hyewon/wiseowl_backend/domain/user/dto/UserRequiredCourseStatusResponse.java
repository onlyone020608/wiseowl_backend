package com.hyewon.wiseowl_backend.domain.user.dto;

import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;

import java.util.List;

public record UserRequiredCourseStatusResponse(
        List<MajorRequiredCourseItemResponse> majorRequiredCourses,
        List<LiberalRequiredCourseItemResponse> liberalRequiredCourses

) {
}
