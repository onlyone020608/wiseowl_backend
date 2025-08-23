package com.hyewon.wiseowl_backend.domain;

import com.hyewon.wiseowl_backend.domain.course.entity.Building;
import com.hyewon.wiseowl_backend.domain.facility.dto.BuildingFacilityResponse;
import com.hyewon.wiseowl_backend.domain.facility.entity.Facility;
import com.hyewon.wiseowl_backend.domain.facility.repository.FacilityRepository;
import com.hyewon.wiseowl_backend.domain.facility.service.FacilityService;
import com.hyewon.wiseowl_backend.global.exception.FacilityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class FacilityServiceTest {

    @Mock private FacilityRepository facilityRepository;
    @InjectMocks private FacilityService facilityService;

    private Building building1;
    private Building building2;
    private Facility facility1;
    private Facility facility2;
    private Facility facility3;

    @BeforeEach
    void setUp() {
        building1 = Building.builder()
                .id(1L)
                .name("백년관")
                .buildingNumber(0)
                .build();
        building2 = Building.builder()
                .id(2L)
                .buildingNumber(1)
                .name("공학관")
                .build();
        facility1 = Facility.builder()
                .building(building1)
                .name("열람실")
                .build();
        facility2 = Facility.builder()
                .name("편의점")
                .floor(1)
                .description("CU 편의점")
                .building(building1)
                .build();
        facility3 = Facility.builder()
                .name("식당")
                .floor(2)
                .building(building2)
                .build();
    }

    @Test
    @DisplayName("getAllFacilities - should return all facilities")
    void getAllFacilities_success() {
        // given
        given(facilityRepository.findAllWithBuilding()).willReturn(List.of(facility1, facility2, facility3));

        // when
        List<BuildingFacilityResponse> response = facilityService.getAllFacilities()
                .stream()
                .sorted(Comparator.comparing(BuildingFacilityResponse::buildingNumber))
                .toList();

        // then
        assertThat(response).hasSize(2);
        assertThat(response.get(0).facilities()).hasSize(2);
        assertThat(response.get(0).buildingNumber()).isEqualTo(0);
        assertThat(response.get(0).buildingName()).isEqualTo("백년관");
    }

    @Test
    @DisplayName("getAllFacilities - should throw FacilityNotFoundException when facility does not exist")
    void getAllFacilities_shouldThrowException_whenFacilityNotFound() {
        // given
        given(facilityRepository.findAll()).willReturn(List.of());

        // when & then
        assertThrows(FacilityNotFoundException.class,
                () -> facilityService.getAllFacilities());
    }
}
