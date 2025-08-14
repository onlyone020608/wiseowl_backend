package com.hyewon.wiseowl_backend.domain.user.dto;

import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;

public record  UserMajorUpdateRequest(MajorType majorType, Long oldMajorId, Long newMajorId) {
}
