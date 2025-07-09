package com.hyewon.wiseowl_backend.domain.user.dto;

import java.util.List;

public record UserRequirementFulfillmentRequest(
        Long majorId,
        List<RequirementStatusUpdate> requirements
) {

}