package com.hyewon.wiseowl_backend.domain.course.dto;

import java.util.List;

public record CourseOfferingsResponse(
        List<CourseOfferingResponse> offerings
) {
    public static CourseOfferingsResponse from(List<CourseOfferingResponse> offerings) {
        return new CourseOfferingsResponse(offerings);
    }
}
