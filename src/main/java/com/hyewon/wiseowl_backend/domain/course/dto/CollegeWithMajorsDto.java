package com.hyewon.wiseowl_backend.domain.course.dto;

import java.util.List;

public record CollegeWithMajorsDto(
        Long collegeId,
        String collegeName,
        List<MajorDto> majors
) {
}
