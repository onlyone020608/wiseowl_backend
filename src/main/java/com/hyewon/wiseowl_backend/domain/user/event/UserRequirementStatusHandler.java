package com.hyewon.wiseowl_backend.domain.user.event;

import com.hyewon.wiseowl_backend.domain.user.service.UserRequirementStatusService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@AllArgsConstructor
public class UserRequirementStatusHandler {
    private final UserRequirementStatusService userRequirementStatusService;

    @TransactionalEventListener
    public void onMajorUpdated(UserMajorUpdateEvent event) {
        userRequirementStatusService.replaceUserRequirementStatusWithMajor(event.getUserId(), event.getRequests());
    }

    @TransactionalEventListener
    public void onMajorTypeUpdated(UserMajorTypeUpdateEvent event) {
        userRequirementStatusService.replaceUserRequirementStatusWithMajorType(event.getUserId(), event.getRequests());
    }
}
