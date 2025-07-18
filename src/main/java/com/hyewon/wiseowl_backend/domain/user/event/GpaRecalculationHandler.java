package com.hyewon.wiseowl_backend.domain.user.event;
import com.hyewon.wiseowl_backend.domain.user.service.GpaRecalculationService;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;



@Component
@AllArgsConstructor
public class GpaRecalculationHandler {

    private final GpaRecalculationService gpaRecalculationService;

    @TransactionalEventListener
    public void onRegistered(CompletedCoursesRegisteredEvent event){

        gpaRecalculationService.recalculateGpa(event.getUserId());

    }
}

