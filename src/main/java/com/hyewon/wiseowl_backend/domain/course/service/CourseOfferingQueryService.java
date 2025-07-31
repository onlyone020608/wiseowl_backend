package com.hyewon.wiseowl_backend.domain.course.service;

import com.hyewon.wiseowl_backend.domain.course.entity.CourseOffering;
import com.hyewon.wiseowl_backend.domain.course.repository.CourseOfferingRepository;
import com.hyewon.wiseowl_backend.global.exception.CourseOfferingNotFoundException;
import com.hyewon.wiseowl_backend.global.exception.MajorNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseOfferingQueryService {
    private final CourseOfferingRepository courseOfferingRepository;

    @Transactional(readOnly = true)
    public CourseOffering getCourseOffering(Long id) {
        return courseOfferingRepository.findById(id)
                .orElseThrow(() -> new CourseOfferingNotFoundException(id));
    }

}
