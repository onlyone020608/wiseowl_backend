package com.hyewon.wiseowl_backend.domain.user.dto;

public record LiberalRequiredCourseItemResponse(
        String liberalCategoryName, boolean fulfilled, int requiredCredit
) {
}
