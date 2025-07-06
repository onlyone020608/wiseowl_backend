package com.hyewon.wiseowl_backend.domain.user.dto;

import java.util.List;

public record CompletedCourseUpdateRequest(
        List<CompletedCourseUpdateItem> courses
) {

}
