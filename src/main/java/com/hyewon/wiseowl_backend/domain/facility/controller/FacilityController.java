package com.hyewon.wiseowl_backend.domain.facility.controller;

import com.hyewon.wiseowl_backend.domain.facility.dto.FacilitiesResponse;
import com.hyewon.wiseowl_backend.domain.facility.service.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/facilities")
@RestController
@RequiredArgsConstructor
public class FacilityController {
    private final FacilityService facilityService;

    @GetMapping
    public ResponseEntity<FacilitiesResponse> getFacilities() {
        return ResponseEntity.ok(facilityService.getAllFacilities());
    }
}
