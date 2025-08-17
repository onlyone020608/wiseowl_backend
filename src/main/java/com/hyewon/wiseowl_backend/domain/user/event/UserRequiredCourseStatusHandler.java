package com.hyewon.wiseowl_backend.domain.user.event;

import com.hyewon.wiseowl_backend.domain.user.service.UserRequiredCourseStatusService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@AllArgsConstructor
public class UserRequiredCourseStatusHandler {
    private final UserRequiredCourseStatusService userRequiredCourseStatusService;

    @TransactionalEventListener
    public void handle(CompletedCoursesRegisteredEvent event) {
        userRequiredCourseStatusService.updateUserRequiredCourseStatus(event.getUserId(), event.getCompletedCourses());
    }

    @TransactionalEventListener
    public void onMajorRegistered(UserMajorRegisteredEvent event) {
        userRequiredCourseStatusService.insertUserRequiredCourseStatus(event.getUserId(), event.getRequests(), event.getEntranceYear());
    }
}
