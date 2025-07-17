package com.hyewon.wiseowl_backend.domain.user.event;

import com.hyewon.wiseowl_backend.domain.user.dto.CreditAndGradeDto;
import com.hyewon.wiseowl_backend.domain.user.entity.Grade;
import com.hyewon.wiseowl_backend.domain.user.entity.Profile;
import com.hyewon.wiseowl_backend.domain.user.repository.ProfileRepository;
import com.hyewon.wiseowl_backend.domain.user.repository.UserCompletedCourseRepository;
import com.hyewon.wiseowl_backend.global.exception.ProfileNotFoundException;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;


@Component
@AllArgsConstructor
public class GpaRecalculationHandler {

    private final UserCompletedCourseRepository userCompletedCourseRepository;
    private final ProfileRepository profileRepository;

    @TransactionalEventListener
    public void handle(CompletedCoursesRegisteredEvent event){
        List<CreditAndGradeDto> result = userCompletedCourseRepository.findCourseCreditsAndGradesByUserId(event.getUserId());

        double totalGradePoints = 0;
        int totalCredits = 0;

        for (CreditAndGradeDto dto : result) {
            int credit = dto.credit();
            Grade grade = dto.grade();
            totalCredits += credit;
            totalGradePoints += credit * grade.getGradePoint();
        }

        double gpa = totalCredits == 0 ? 0 : totalGradePoints / totalCredits;
        Profile profile = profileRepository.findByUserId(event.getUserId()).orElseThrow(ProfileNotFoundException::new);
        profile.updateGPA(gpa);


    }
}

