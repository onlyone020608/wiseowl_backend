package com.hyewon.wiseowl_backend.domain.facility.dto;

import com.hyewon.wiseowl_backend.domain.facility.entity.Facility;
import com.hyewon.wiseowl_backend.domain.facility.entity.FacilityCategory;

public record FacilityResponse(
        String name,
        Integer floor,
        FacilityCategory facilityCategory,
        String description

) {
    public static FacilityResponse from(Facility facility) {
        return new FacilityResponse(
                facility.getName(),
                facility.getFloor(),
                facility.getFacilityCategory(),
                facility.getDescription()
        );
    }
}
