package com.hyewon.wiseowl_backend.domain.user.repository;

import com.hyewon.wiseowl_backend.domain.user.dto.CreditAndGradeDto;

import java.util.List;

public interface UserCompletedCourseQueryRepository {
    List<CreditAndGradeDto> findCourseCreditsAndGradesByUserId(Long userId);
    int sumCreditsByUserAndMajor(Long userId, Long majorId);
    int sumCreditsByUserAndLiberalCategory(Long userId, Long liberalCategoryId);
}
