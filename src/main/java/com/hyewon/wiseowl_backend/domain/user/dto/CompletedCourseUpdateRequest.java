package com.hyewon.wiseowl_backend.domain.user.dto;

import com.hyewon.wiseowl_backend.domain.user.entity.Grade;

public record CompletedCourseUpdateRequest(Long userCompletedCourseId, Grade grade, Boolean retake) {
}
