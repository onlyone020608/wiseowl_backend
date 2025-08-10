package com.hyewon.wiseowl_backend.domain.user.repository;

import com.hyewon.wiseowl_backend.domain.user.entity.UserTrack;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTrackRepository extends JpaRepository<UserTrack, Long> {
    UserTrack findByUserId(Long userId);
}
