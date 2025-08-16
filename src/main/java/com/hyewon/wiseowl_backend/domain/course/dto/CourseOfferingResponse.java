package com.hyewon.wiseowl_backend.domain.course.dto;

import com.hyewon.wiseowl_backend.domain.course.entity.CourseOffering;

public record CourseOfferingResponse(Long id, Long majorId, Long liberalCategoryId, String courseName, String professor,
                                     String classTime, String courseCode, String room) {
    public static CourseOfferingResponse from(CourseOffering offering, Long liberalCategoryId) {
        return new CourseOfferingResponse(
                offering.getId(), offering.getCourse().getMajor()!=null ? offering.getCourse().getMajor().getId() : null,
                liberalCategoryId, offering.getCourse().getName(),
                offering.getProfessor(), offering.getClassTime(), offering.getCourseCode(), offering.getRoom()
        );
    }
}
