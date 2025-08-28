package com.hyewon.wiseowl_backend.domain.user.dto;

import com.hyewon.wiseowl_backend.domain.user.entity.Grade;
import com.hyewon.wiseowl_backend.domain.user.entity.UserCompletedCourse;

public record UserCompletedCourseItem(Long userCompletedCourseId, Grade grade, boolean retake, String courseName, Integer credit) {
    public static UserCompletedCourseItem from(UserCompletedCourse userCompletedCourse) {
        return new UserCompletedCourseItem(
                userCompletedCourse.getId(),
                userCompletedCourse.getGrade(),
                userCompletedCourse.isRetake(),
                userCompletedCourse.getCourseOffering().getCourse().getName(),
                userCompletedCourse.getCourseOffering().getCourse().getCredit()
        );
    }
}
