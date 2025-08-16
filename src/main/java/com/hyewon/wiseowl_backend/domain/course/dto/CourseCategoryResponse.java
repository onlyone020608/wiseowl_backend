package com.hyewon.wiseowl_backend.domain.course.dto;

import com.hyewon.wiseowl_backend.domain.course.entity.LiberalCategory;
import com.hyewon.wiseowl_backend.domain.course.entity.Major;

public record CourseCategoryResponse(Long id, String name, String type) {

    public static CourseCategoryResponse fromMajor(Major major) {
        return new CourseCategoryResponse(major.getId(), major.getName(),"MAJOR");
    }

    public static CourseCategoryResponse fromLiberal(LiberalCategory liberal) {
        return new CourseCategoryResponse(liberal.getId(), liberal.getName(),"LIBERAL");
    }
}
