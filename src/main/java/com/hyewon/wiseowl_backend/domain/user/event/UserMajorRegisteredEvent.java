package com.hyewon.wiseowl_backend.domain.user.event;

import com.hyewon.wiseowl_backend.domain.user.dto.UserMajorRequest;
import lombok.Getter;

import java.util.List;

@Getter
public class UserMajorRegisteredEvent {
    private final Long userId;
    private final List<UserMajorRequest> requests;
    private final Integer entranceYear;

    public UserMajorRegisteredEvent(Long userId, List<UserMajorRequest> requests, Integer entranceYear) {
        this.userId = userId;
        this.requests = requests;
        this.entranceYear = entranceYear;
    }
}
