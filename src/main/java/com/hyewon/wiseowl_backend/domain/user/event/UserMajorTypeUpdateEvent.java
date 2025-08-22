package com.hyewon.wiseowl_backend.domain.user.event;

import com.hyewon.wiseowl_backend.domain.user.dto.UserMajorTypeUpdateItem;
import lombok.Getter;

import java.util.List;

@Getter
public class UserMajorTypeUpdateEvent {
    private final Long userId;
    private final List<UserMajorTypeUpdateItem> requests;

    public UserMajorTypeUpdateEvent(Long userId, List<UserMajorTypeUpdateItem> requests) {
        this.userId = userId;
        this.requests = requests;
    }
}
