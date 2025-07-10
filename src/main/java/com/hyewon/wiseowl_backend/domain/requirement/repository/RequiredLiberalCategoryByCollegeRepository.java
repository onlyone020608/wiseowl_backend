package com.hyewon.wiseowl_backend.domain.requirement.repository;

import com.hyewon.wiseowl_backend.domain.requirement.entity.RequiredLiberalCategoryByCollege;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequiredLiberalCategoryByCollegeRepository extends JpaRepository<RequiredLiberalCategoryByCollege, Long>, RequiredLiberalCategoryQueryRepository{

}
