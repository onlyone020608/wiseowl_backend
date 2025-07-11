package com.hyewon.wiseowl_backend.domain.user.dto;

public record GraduationRequirementItemResponse(
        String requirementName,
        String description,
        boolean fulfilled
) {
}
