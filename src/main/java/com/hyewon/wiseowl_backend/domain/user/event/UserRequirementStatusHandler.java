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
        userRequirementStatusService.replaceUserRequirementStatus(event.getUserId());
    }

    @TransactionalEventListener
    public void onMajorTypeUpdated(UserMajorTypeUpdateEvent event) {
        userRequirementStatusService.replaceUserRequirementStatus(event.getUserId());
    }

    @TransactionalEventListener
    public void onRegistered(UserMajorRegisteredEvent event){
        userRequirementStatusService.replaceUserRequirementStatus(event.getUserId());
    }
}
