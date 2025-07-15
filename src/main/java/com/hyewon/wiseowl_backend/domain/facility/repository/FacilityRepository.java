package com.hyewon.wiseowl_backend.domain.facility.repository;

import com.hyewon.wiseowl_backend.domain.facility.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacilityRepository extends JpaRepository <Facility, Long> {
}
