package com.hyewon.wiseowl_backend.domain.user.dto;

public record RequirementStatusSummary(Long userRequirementStatusId,
                                       String requirementName,
                                       boolean fulfilled) {
}
