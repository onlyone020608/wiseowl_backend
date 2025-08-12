package com.hyewon.wiseowl_backend.domain.user.dto;

import com.hyewon.wiseowl_backend.domain.requirement.entity.Requirement;
import com.hyewon.wiseowl_backend.domain.user.entity.UserRequirementStatus;

public record UserRequirementStatusItem(Long userRequirementStatusId,
                                        String name,
                                        String description,
                                        boolean fulfilled) {

    public static UserRequirementStatusItem from(UserRequirementStatus status) {
        Requirement r = status.getMajorRequirement().getRequirement();

        return new UserRequirementStatusItem(
                status.getId(),
                r.getName(),
                status.getMajorRequirement().getDescription(),
                status.isFulfilled()
        );
    }
}
