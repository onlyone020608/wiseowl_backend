package com.hyewon.wiseowl_backend.domain.user.dto;

import java.util.List;

public record GraduationRequirementsResponse(
        List<GraduationRequirementGroupByMajorResponse> requirements
) {
    public static GraduationRequirementsResponse from(
            List<GraduationRequirementGroupByMajorResponse> groups
    ) {
        return new GraduationRequirementsResponse(groups);
    }
}
