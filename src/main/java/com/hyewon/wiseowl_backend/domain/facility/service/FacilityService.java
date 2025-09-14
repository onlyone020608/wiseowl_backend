package com.hyewon.wiseowl_backend.domain.facility.service;

import com.hyewon.wiseowl_backend.domain.facility.dto.BuildingFacilityResponse;
import com.hyewon.wiseowl_backend.domain.facility.dto.FacilitiesResponse;
import com.hyewon.wiseowl_backend.domain.facility.dto.FacilityResponse;
import com.hyewon.wiseowl_backend.domain.facility.entity.Facility;
import com.hyewon.wiseowl_backend.domain.facility.repository.FacilityRepository;
import com.hyewon.wiseowl_backend.global.exception.FacilityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FacilityService {
    private final FacilityRepository facilityRepository;

    @Cacheable(value = "facilities")
    @Transactional(readOnly = true)
    public FacilitiesResponse getAllFacilities() {
        List<Facility> facilities = facilityRepository.findAllWithBuilding();
        if (facilities.isEmpty()) {
            throw new FacilityNotFoundException();
        }

        List<BuildingFacilityResponse> result = facilities.stream()
                .collect(Collectors.groupingBy(Facility::getBuilding))
                .entrySet().stream()
                .sorted(Comparator.comparing(entry -> entry.getKey().getBuildingNumber()))
                .map(entry -> new BuildingFacilityResponse(
                        entry.getKey().getBuildingNumber(),
                        entry.getKey().getName(),
                        entry.getValue().stream()
                                .map(FacilityResponse::from)
                                .sorted(Comparator.comparing(FacilityResponse::name))
                                .toList()
                ))
                .toList();

        return FacilitiesResponse.from(result);
    }
}