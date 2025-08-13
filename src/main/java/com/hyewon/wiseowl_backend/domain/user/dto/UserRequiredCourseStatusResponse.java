package com.hyewon.wiseowl_backend.domain.user.dto;

import java.util.List;

public record UserRequiredCourseStatusResponse(
        List<MajorRequiredCourseItemResponse> majorRequiredCourses,
        List<LiberalRequiredCourseItemResponse> liberalRequiredCourses
) {
}
