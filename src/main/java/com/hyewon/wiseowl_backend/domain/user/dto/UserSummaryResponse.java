package com.hyewon.wiseowl_backend.domain.user.dto;



public record UserSummaryResponse(String userName, String studentId, double GPA, UserMajorDetail primaryMajor, UserMajorDetail doubleMajor) {

}
