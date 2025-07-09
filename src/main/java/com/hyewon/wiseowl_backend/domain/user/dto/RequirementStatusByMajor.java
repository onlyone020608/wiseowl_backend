package com.hyewon.wiseowl_backend.domain.user.dto;

import java.util.List;

public record RequirementStatusByMajor(
        String majorName,
        int earnedCredits,
        int requiredCredits,
        List<RequirementStatusSummary> requirements


) {
}
