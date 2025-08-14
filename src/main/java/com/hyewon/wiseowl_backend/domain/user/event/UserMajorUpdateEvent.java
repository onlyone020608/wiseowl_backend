package com.hyewon.wiseowl_backend.domain.user.event;

import com.hyewon.wiseowl_backend.domain.user.dto.UserMajorUpdateRequest;
import lombok.Getter;

import java.util.List;

@Getter
public class UserMajorUpdateEvent {
    private final Long userId;
    private final List<UserMajorUpdateRequest> requests;

    public UserMajorUpdateEvent(Long userId, List<UserMajorUpdateRequest> requests) {
        this.userId = userId;
        this.requests = requests;
    }
}
