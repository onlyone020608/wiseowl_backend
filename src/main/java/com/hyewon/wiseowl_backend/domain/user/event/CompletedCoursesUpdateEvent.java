package com.hyewon.wiseowl_backend.domain.user.event;

import lombok.Getter;

@Getter
public class CompletedCoursesUpdateEvent {
    private final Long userId;

    public CompletedCoursesUpdateEvent(Long userId){
        this.userId = userId;
    }
}
