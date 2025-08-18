package com.hyewon.wiseowl_backend.domain.requirement.service;

import com.hyewon.wiseowl_backend.domain.requirement.entity.RequiredMajorCourse;
import com.hyewon.wiseowl_backend.domain.requirement.repository.CourseCreditTransferRuleRepository;
import com.hyewon.wiseowl_backend.domain.user.entity.UserCompletedCourse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseCreditTransferRuleService {
    private final CourseCreditTransferRuleRepository creditTransferRuleRepository ;

    @Transactional(readOnly = true)
    public boolean isTransferable(UserCompletedCourse cc, RequiredMajorCourse required) {
        return creditTransferRuleRepository.isCourseTransferable(
                cc.getCourseOffering().getCourse().getId(),
                required.getCourse().getId(),
                cc.getCourseOffering().getSemester().getYear()
        );
    }
}
