package com.hyewon.wiseowl_backend.fixture;

import com.hyewon.wiseowl_backend.domain.course.entity.Major;
import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorRequirement;
import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;
import com.hyewon.wiseowl_backend.domain.requirement.entity.RequiredLiberalCategory;
import com.hyewon.wiseowl_backend.domain.requirement.entity.Requirement;

public class RequirementFixture {
    public static Requirement aRequirement() {
        return Requirement.builder()
                .name("졸업시험")
                .build();
    }

    public static MajorRequirement aMajorRequirement(Major major) {
        return MajorRequirement.builder()
                .major(major)
                .requirement(aRequirement())
                .majorType(MajorType.PRIMARY)
                .description("다른시험대체가능")
                .build();
    }

    public static RequiredLiberalCategory aRequiredLiberalCategory(Major major) {
        return RequiredLiberalCategory.builder()
                .major(major)
                .requiredCredit(6)
                .build();
    }
}
