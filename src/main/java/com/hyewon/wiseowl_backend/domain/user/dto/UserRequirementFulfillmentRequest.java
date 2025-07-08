package com.hyewon.wiseowl_backend.domain.user.dto;

import java.util.List;

public record UserRequirementFulfillmentRequest(
        Long majorId,
        List<UserRequirementStatusUpdate> requirements
) {
    public record UserRequirementStatusUpdate(
            Long userRequirementStatusId,
            boolean fulfilled
    ) {}
}