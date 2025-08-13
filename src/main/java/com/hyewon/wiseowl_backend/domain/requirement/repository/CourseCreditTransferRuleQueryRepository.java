package com.hyewon.wiseowl_backend.domain.requirement.repository;

public interface CourseCreditTransferRuleQueryRepository {
    boolean isCourseTransferable(Long fromCourseId, Long toCourseId, Integer entryYear);
}
