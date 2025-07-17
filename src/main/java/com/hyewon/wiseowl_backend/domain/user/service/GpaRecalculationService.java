package com.hyewon.wiseowl_backend.domain.user.service;

import com.hyewon.wiseowl_backend.domain.user.dto.CreditAndGradeDto;
import com.hyewon.wiseowl_backend.domain.user.entity.Grade;
import com.hyewon.wiseowl_backend.domain.user.entity.Profile;
import com.hyewon.wiseowl_backend.domain.user.repository.ProfileRepository;
import com.hyewon.wiseowl_backend.domain.user.repository.UserCompletedCourseRepository;
import com.hyewon.wiseowl_backend.global.exception.ProfileNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GpaRecalculationService {

    private final UserCompletedCourseRepository userCompletedCourseRepository;
    private final ProfileRepository profileRepository;

    @Transactional
    public void recalculateGpa(Long userId){
        List<CreditAndGradeDto> result = userCompletedCourseRepository.findCourseCreditsAndGradesByUserId(userId);

        double totalGradePoints = 0;
        int totalCredits = 0;

        for (CreditAndGradeDto dto : result) {
            int credit = dto.credit();
            Grade grade = dto.grade();
            totalCredits += credit;
            totalGradePoints += credit * grade.getGradePoint();
        }

        double gpa = totalCredits == 0 ? 0 : totalGradePoints / totalCredits;
        Profile profile = profileRepository.findByUserId(userId).orElseThrow(ProfileNotFoundException::new);
        profile.updateGPA(gpa);

    }
}
