package com.hyewon.wiseowl_backend.domain.user.event;

import com.hyewon.wiseowl_backend.domain.user.service.UserRequiredCourseStatusService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@AllArgsConstructor
public class UserRequiredCourseStatusHandler {
    private final UserRequiredCourseStatusService userRequiredCourseStatusService;

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(CompletedCoursesRegisteredEvent event) {
        userRequiredCourseStatusService.updateUserRequiredCourseStatus(event.getUserId(), event.getCompletedCourses());
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onMajorRegistered(UserMajorRegisteredEvent event) {
        userRequiredCourseStatusService.replaceUserRequiredCourseStatus(event.getUserId());
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onMajorUpdated(UserMajorUpdateEvent event) {
        userRequiredCourseStatusService.replaceUserRequiredCourseStatus(event.getUserId());
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onMajorTypeUpdated(UserMajorTypeUpdateEvent event) {
        userRequiredCourseStatusService.replaceUserRequiredCourseStatus(event.getUserId());
    }
}
