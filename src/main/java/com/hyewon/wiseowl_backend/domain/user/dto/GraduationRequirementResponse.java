package com.hyewon.wiseowl_backend.domain.user.dto;

import com.hyewon.wiseowl_backend.domain.requirement.entity.Requirement;
import com.hyewon.wiseowl_backend.domain.user.entity.UserRequirementStatus;

public record GraduationRequirementResponse( Long userRequirementStatusId, String name,
                                             String description,
                                             boolean fulfilled){
    public static GraduationRequirementResponse from(UserRequirementStatus status) {
        Requirement r = status.getMajorRequirement().getRequirement();
        return new GraduationRequirementResponse(
                status.getId(),
                r.getName(),
                status.getMajorRequirement().getDescription(),
                status.isFulfilled()
        );
    }



}
