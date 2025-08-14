package com.hyewon.wiseowl_backend.domain.user.dto;

public record UserSummaryResponse(String username, String studentId, double gpa,
                                  UserMajorDetail primaryMajor, UserMajorDetail doubleMajor) {
}
