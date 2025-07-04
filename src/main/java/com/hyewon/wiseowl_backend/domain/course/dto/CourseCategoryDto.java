package com.hyewon.wiseowl_backend.domain.course.dto;

import com.hyewon.wiseowl_backend.domain.course.entity.LiberalCategory;
import com.hyewon.wiseowl_backend.domain.course.entity.Major;

public record CourseCategoryDto(Long id, String name, String type) {

    public static CourseCategoryDto fromMajor(Major major) {
        return new CourseCategoryDto(major.getId(), major.getName(),"MAJOR");
    }

    public static CourseCategoryDto fromLiberal(LiberalCategory liberal){
        return new CourseCategoryDto(liberal.getId(), liberal.getName(),"LIBRERAL");
    }


}
