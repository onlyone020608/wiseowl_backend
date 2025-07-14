package com.hyewon.wiseowl_backend.domain.user.event;

import com.hyewon.wiseowl_backend.domain.user.entity.UserCompletedCourse;
import lombok.Getter;

import java.util.List;

@Getter
public class CompletedCoursesRegisteredEvent {
    private final Long userId;
    private final List<UserCompletedCourse> completedCourses;


    public CompletedCoursesRegisteredEvent(Long userId, List<UserCompletedCourse> completedCourses) {
        this.userId = userId;
        this.completedCourses = completedCourses;

    }
}
