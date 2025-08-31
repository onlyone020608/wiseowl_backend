package com.hyewon.wiseowl_backend.domain.user.repository;

import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;
import com.hyewon.wiseowl_backend.domain.user.entity.UserMajor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserMajorRepository extends JpaRepository<UserMajor, Long>, UserMajorQueryRepository {
    @Query("select um from UserMajor um join fetch um.major where um.user.id = :userId")
    List<UserMajor> findAllByUserIdWithMajor(@Param("userId") Long userId);
    UserMajor findByUserIdAndMajorType(Long userId, MajorType majorType);
    boolean existsByUserIdAndMajorType(Long userId, MajorType majorType);
    boolean existsByUserId(Long userId);
}
