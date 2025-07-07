package com.hyewon.wiseowl_backend.domain.user.dto;

import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;
import jakarta.validation.constraints.NotNull;

public record UserMajorRequest(
        @NotNull Long majorId,
        @NotNull MajorType majorType) {

}
