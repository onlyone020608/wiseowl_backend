package com.hyewon.wiseowl_backend.domain.user.dto;

import com.hyewon.wiseowl_backend.domain.requirement.entity.Track;

import java.util.List;

public record UserMajorTypeUpdateRequest(List<UserMajorTypeUpdateItem> userMajorTypeUpdateItems, Track track) {
}
