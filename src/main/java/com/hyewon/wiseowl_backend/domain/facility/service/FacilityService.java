package com.hyewon.wiseowl_backend.domain.facility.service;

import com.hyewon.wiseowl_backend.domain.course.entity.Building;
import com.hyewon.wiseowl_backend.domain.facility.entity.Facility;
import com.hyewon.wiseowl_backend.domain.facility.dto.BuildingFacilityResponse;
import com.hyewon.wiseowl_backend.domain.facility.dto.FacilityResponse;
import com.hyewon.wiseowl_backend.domain.facility.repository.FacilityRepository;
import com.hyewon.wiseowl_backend.global.exception.FacilityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FacilityService {

    private final FacilityRepository facilityRepository;

    @Transactional(readOnly = true)
    public List<BuildingFacilityResponse> fetchAllFacilities(){
        List<Facility> all = facilityRepository.findAll();
        if(all.isEmpty()){
            throw new FacilityNotFoundException();
        }
        Map<Building, List<Facility>> grouped = all.stream()
                .collect(Collectors.groupingBy(Facility::getBuilding));
        return grouped.entrySet().stream().map(
                entry -> {
                    Building building = entry.getKey();
                    List<FacilityResponse> facilities = entry.getValue().stream().map(
                            FacilityResponse::from
                    ).toList();
                    return new BuildingFacilityResponse(
                            building.getBuildingNumber(),
                            building.getName(),
                            facilities
                    );

                }
        ).toList();
    }
}
