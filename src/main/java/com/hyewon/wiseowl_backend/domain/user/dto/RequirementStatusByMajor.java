package com.hyewon.wiseowl_backend.domain.user.dto;

import com.hyewon.wiseowl_backend.domain.user.entity.UserRequirementStatus;

import java.util.List;

public record RequirementStatusByMajor(
        String majorName,
        int earnedCredits,
        int requiredCredits,
        List<UserRequirementStatusItem> requirements) {

    public static RequirementStatusByMajor from(
            String majorName,
            int earnedCredits,
            int requiredCredits,
            List<UserRequirementStatus> statuses
    ) {
        return new RequirementStatusByMajor(
                majorName,
                earnedCredits,
                requiredCredits,
                statuses.stream()
                        .map(UserRequirementStatusItem::from)
                        .toList()
        );
    }
}
