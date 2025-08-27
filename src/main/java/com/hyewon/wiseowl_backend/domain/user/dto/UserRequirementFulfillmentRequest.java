package com.hyewon.wiseowl_backend.domain.user.dto;

public record UserRequirementFulfillmentRequest(Long userRequirementStatusId,
        boolean fulfilled) {
}