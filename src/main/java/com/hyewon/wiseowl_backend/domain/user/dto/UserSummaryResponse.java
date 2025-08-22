package com.hyewon.wiseowl_backend.domain.user.dto;

public record UserSummaryResponse(String username, Integer entranceYear, double gpa,
                                  UserMajorDetail primaryMajor, UserMajorDetail doubleMajor) {
}
