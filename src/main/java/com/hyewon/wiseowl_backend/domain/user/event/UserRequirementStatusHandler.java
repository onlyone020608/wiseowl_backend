package com.hyewon.wiseowl_backend.domain.user.event;

import com.hyewon.wiseowl_backend.domain.user.service.UserRequirementStatusService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@AllArgsConstructor
public class UserRequirementStatusHandler {
    private final UserRequirementStatusService userRequirementStatusService;

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onMajorUpdated(UserMajorUpdateEvent event) {
        userRequirementStatusService.replaceUserRequirementStatus(event.getUserId());
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onMajorTypeUpdated(UserMajorTypeUpdateEvent event) {
        userRequirementStatusService.replaceUserRequirementStatus(event.getUserId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onRegistered(UserMajorRegisteredEvent event){
        userRequirementStatusService.replaceUserRequirementStatus(event.getUserId());
    }
}
