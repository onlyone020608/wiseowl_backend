package com.hyewon.wiseowl_backend.domain.user.dto;

import java.util.List;

public record UserCompletedCoursesResponse(
        List<UserCompletedCourseBySemesterResponse> semesters
) {
    public static UserCompletedCoursesResponse from(
            List<UserCompletedCourseBySemesterResponse> semesters
    ) {
        return new UserCompletedCoursesResponse(semesters);
    }
}