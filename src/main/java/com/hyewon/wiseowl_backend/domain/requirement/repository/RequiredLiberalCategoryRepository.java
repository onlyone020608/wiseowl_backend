package com.hyewon.wiseowl_backend.domain.requirement.repository;

import com.hyewon.wiseowl_backend.domain.requirement.entity.RequiredLiberalCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RequiredLiberalCategoryRepository extends JpaRepository<RequiredLiberalCategory, Long>, RequiredLiberalCategoryQueryRepository {
    @Query("select r from RequiredLiberalCategory r join fetch r.liberalCategory where r.id = :id")
    Optional<RequiredLiberalCategory> findByIdWithLiberalCategory(@Param("id") Long id);
}
