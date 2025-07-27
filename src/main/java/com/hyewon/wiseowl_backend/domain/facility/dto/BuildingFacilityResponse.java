package com.hyewon.wiseowl_backend.domain.facility.dto;

import java.util.List;

public record BuildingFacilityResponse(
        Integer buildingNumber,
        String buildingName,
        List<FacilityResponse> facilities
) {
}
