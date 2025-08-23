package com.hyewon.wiseowl_backend.domain.user.repository;

import com.hyewon.wiseowl_backend.domain.user.entity.UserRequirementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRequirementStatusRepository extends JpaRepository<UserRequirementStatus, Long>, UserRequirementStatusQueryRepository {
    @Query("select urs from UserRequirementStatus urs " +
            "join fetch urs.majorRequirement mr " +
            "join fetch mr.major m " +
            "where urs.user.id = :userId")
    List<UserRequirementStatus> findAllByUserIdWithMajor(@Param("userId") Long userId);
    void deleteAllByUserId(Long userId);
}
