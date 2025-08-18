package com.hyewon.wiseowl_backend.domain.requirement.repository;

import com.hyewon.wiseowl_backend.domain.requirement.entity.RequiredLiberalCategoryByCollege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RequiredLiberalCategoryByCollegeRepository extends JpaRepository<RequiredLiberalCategoryByCollege, Long>, RequiredLiberalCategoryQueryRepository {
    @Query("select r from RequiredLiberalCategoryByCollege r join fetch r.liberalCategory where r.id = :id")
    Optional<RequiredLiberalCategoryByCollege> findByIdWithLiberalCategory(@Param("id") Long id);
}
