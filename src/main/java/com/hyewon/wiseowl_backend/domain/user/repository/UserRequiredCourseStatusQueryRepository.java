package com.hyewon.wiseowl_backend.domain.user.repository;

import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;
import com.hyewon.wiseowl_backend.domain.user.dto.LiberalRequiredCourseItemResponse;
import com.hyewon.wiseowl_backend.domain.user.dto.MajorRequiredCourseItemResponse;

import java.util.List;

public interface UserRequiredCourseStatusQueryRepository {
    List<MajorRequiredCourseItemResponse> findMajorItems(Long userId, MajorType majorType);
    List<LiberalRequiredCourseItemResponse> findLiberalItems(Long userId);
}
