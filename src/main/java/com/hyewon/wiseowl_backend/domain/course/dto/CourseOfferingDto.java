package com.hyewon.wiseowl_backend.domain.course.dto;


import com.hyewon.wiseowl_backend.domain.course.entity.CourseOffering;

public record CourseOfferingDto(Long id, Long majorId, Long liberalCategoryId, String courseName, String professor,
                                String classTime, String courseCode, String room) {
    public static CourseOfferingDto from(CourseOffering offering, Long liberalCategoryId) {
        return new CourseOfferingDto(
                offering.getId(), offering.getCourse().getMajor()!=null ? offering.getCourse().getMajor().getId() : null,
                liberalCategoryId, offering.getCourse().getName(),
                offering.getProfessor(), offering.getClassTime(), offering.getCourseCode(), offering.getRoom()
        );
    }


}
