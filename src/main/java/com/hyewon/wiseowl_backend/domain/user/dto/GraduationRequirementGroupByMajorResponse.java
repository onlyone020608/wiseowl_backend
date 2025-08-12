package com.hyewon.wiseowl_backend.domain.user.dto;

import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;
import com.hyewon.wiseowl_backend.domain.user.entity.UserRequirementStatus;

import java.util.List;

public record GraduationRequirementGroupByMajorResponse(
        Long majorId,
        String majorName,
        MajorType majorType,
        List<UserRequirementStatusItem> requirements
) {
    public static GraduationRequirementGroupByMajorResponse from(
            Long majorId,
            String majorName,
            MajorType majorType,
            List<UserRequirementStatus> statuses
    ) {
        return new GraduationRequirementGroupByMajorResponse(
                majorId,
                majorName,
                majorType,
                statuses.stream()
                        .map(UserRequirementStatusItem::from)
                        .toList()
        );
    }
}
