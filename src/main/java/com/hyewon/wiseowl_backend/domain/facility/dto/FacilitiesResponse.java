package com.hyewon.wiseowl_backend.domain.facility.dto;

import java.util.List;

public record FacilitiesResponse(
        List<BuildingFacilityResponse> buildings
) {
    public static FacilitiesResponse from(List<BuildingFacilityResponse> buildings) {
        return new FacilitiesResponse(buildings);
    }
}
