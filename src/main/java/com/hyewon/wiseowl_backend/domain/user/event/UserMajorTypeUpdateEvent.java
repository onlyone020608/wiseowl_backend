package com.hyewon.wiseowl_backend.domain.user.event;

import com.hyewon.wiseowl_backend.domain.user.dto.UserMajorTypeUpdateRequest;
import lombok.Getter;

import java.util.List;

@Getter
public class UserMajorTypeUpdateEvent {
    private final Long userId;
    private final List<UserMajorTypeUpdateRequest> requests;

    public UserMajorTypeUpdateEvent(Long userId, List<UserMajorTypeUpdateRequest> requests) {
        this.userId = userId;
        this.requests = requests;
    }
}
