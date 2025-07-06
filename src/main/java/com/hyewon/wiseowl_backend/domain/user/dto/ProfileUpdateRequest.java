package com.hyewon.wiseowl_backend.domain.user.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ProfileUpdateRequest(
        @NotBlank String name,
        @NotNull Integer entranceYear,
        List<UserMajorRequest> majors
){



}
