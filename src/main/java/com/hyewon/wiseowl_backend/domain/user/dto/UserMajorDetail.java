package com.hyewon.wiseowl_backend.domain.user.dto;

import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;

public record UserMajorDetail(Long userMajorId, Long collegeId, String collegeName, Long majorId, String majorName, MajorType majorType) {
}
