package com.hyewon.wiseowl_backend.domain.course.dto;

import java.util.List;

public record CollegesWithMajorsResponse(
        List<CollegeWithMajorsResponse> colleges
) {
    public static CollegesWithMajorsResponse from(List<CollegeWithMajorsResponse> colleges) {
        return new CollegesWithMajorsResponse(colleges);
    }
}
