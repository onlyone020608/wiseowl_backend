package com.hyewon.wiseowl_backend.domain.user.dto;

import com.hyewon.wiseowl_backend.domain.requirement.entity.Track;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ProfileUpdateRequest(
        @NotBlank String name,
        @NotNull Integer entranceYear,
        List<UserMajorRequest> majors,
        Track track
) {
}
