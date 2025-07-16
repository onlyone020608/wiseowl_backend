package com.hyewon.wiseowl_backend.domain.notice.repository;

import com.hyewon.wiseowl_backend.domain.notice.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
}
