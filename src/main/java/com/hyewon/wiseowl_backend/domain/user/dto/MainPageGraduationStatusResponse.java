package com.hyewon.wiseowl_backend.domain.user.dto;

import java.util.List;

public record MainPageGraduationStatusResponse(
        String username,
        List<RequirementStatusByMajor> requirementStatuses

) {
}
