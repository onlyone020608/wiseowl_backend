package com.hyewon.wiseowl_backend.domain.requirement.service;

import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;
import com.hyewon.wiseowl_backend.domain.requirement.entity.RequiredMajorCourse;
import com.hyewon.wiseowl_backend.domain.requirement.repository.RequiredMajorCourseRepository;
import com.hyewon.wiseowl_backend.global.exception.RequiredMajorCourseNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RequiredMajorCourseQueryService {
    private final RequiredMajorCourseRepository requiredMajorCourseRepository;

    @Transactional(readOnly = true)
    public List<RequiredMajorCourse> getApplicableMajorCourses(Long majorId, MajorType type, int entranceYear){
        return requiredMajorCourseRepository.findApplicableMajorCourses(majorId, type, entranceYear);
    }

    @Transactional(readOnly = true)
    public RequiredMajorCourse getRequiredMajorCourse(Long requiredMajorCourseId){
        return requiredMajorCourseRepository.findById(requiredMajorCourseId).orElseThrow(() -> new RequiredMajorCourseNotFoundException(requiredMajorCourseId));
    }


}
