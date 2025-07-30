package com.hyewon.wiseowl_backend.domain.course.service;

import com.hyewon.wiseowl_backend.domain.course.entity.Major;
import com.hyewon.wiseowl_backend.domain.course.repository.MajorRepository;
import com.hyewon.wiseowl_backend.global.exception.MajorNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MajorQueryService {
    private final MajorRepository majorRepository;

    @Transactional(readOnly = true)
    public String getMajorName(Long id) {
        return majorRepository.findById(id)
                .orElseThrow(() -> new MajorNotFoundException(id))
                .getName();
    }

    @Transactional(readOnly = true)
    public Major getMajor(Long id) {
        return majorRepository.findById(id)
                .orElseThrow(() -> new MajorNotFoundException(id));
    }
}
