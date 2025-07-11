package com.hyewon.wiseowl_backend.domain.user.dto;

public record MajorRequiredCourseItemResponse(
        String courseCode, String courseName, boolean fulfilled
) {
}
