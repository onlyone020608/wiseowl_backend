package com.hyewon.wiseowl_backend.domain.user.dto;

import com.hyewon.wiseowl_backend.domain.requirement.entity.Track;

public record UserSummaryResponse(String username, Integer entranceYear, double gpa,
                                  UserMajorDetail primaryMajor, UserMajorDetail doubleMajor, Track track) {
}
