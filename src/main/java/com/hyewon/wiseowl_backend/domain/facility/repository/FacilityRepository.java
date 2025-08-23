package com.hyewon.wiseowl_backend.domain.facility.repository;

import com.hyewon.wiseowl_backend.domain.facility.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FacilityRepository extends JpaRepository <Facility, Long> {
    @Query("select f from Facility f join fetch f.building")
    List<Facility> findAllWithBuilding();
}
