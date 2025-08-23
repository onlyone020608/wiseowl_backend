package com.hyewon.wiseowl_backend.domain.facility.service;

import com.hyewon.wiseowl_backend.domain.facility.dto.BuildingFacilityResponse;
import com.hyewon.wiseowl_backend.domain.facility.dto.FacilityResponse;
import com.hyewon.wiseowl_backend.domain.facility.entity.Facility;
import com.hyewon.wiseowl_backend.domain.facility.repository.FacilityRepository;
import com.hyewon.wiseowl_backend.global.exception.FacilityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FacilityService {
    private final FacilityRepository facilityRepository;

    @Transactional(readOnly = true)
    public List<BuildingFacilityResponse> getAllFacilities() {
        List<Facility> facilities = facilityRepository.findAllWithBuilding();
        if (facilities.isEmpty()) {
            throw new FacilityNotFoundException();
        }

        return facilities.stream()
                .collect(Collectors.groupingBy(Facility::getBuilding,
                        Collectors.mapping(FacilityResponse::from, Collectors.toList())))
                .entrySet().stream()
                .map(entry -> new BuildingFacilityResponse(
                        entry.getKey().getBuildingNumber(),
                        entry.getKey().getName(),
                        entry.getValue()
                        ))
                .toList();
    }
}