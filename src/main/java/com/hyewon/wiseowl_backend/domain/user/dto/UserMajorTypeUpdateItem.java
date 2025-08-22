package com.hyewon.wiseowl_backend.domain.user.dto;

import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;

public record UserMajorTypeUpdateItem(Long userMajorId, MajorType oldMajorType, MajorType newMajorType) {
}
