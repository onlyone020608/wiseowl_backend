package com.hyewon.wiseowl_backend.domain.user.event;

import com.hyewon.wiseowl_backend.domain.user.service.RequirementStatusUpdateService;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@AllArgsConstructor
public class RequirementStatusUpdater {
    private final RequirementStatusUpdateService requirementStatusUpdateService;

    @TransactionalEventListener
    public void handle(CompletedCoursesRegisteredEvent event) {
        requirementStatusUpdateService.updateRequirementStatus(event.getUserId(), event.getCompletedCourses());
    }
}
