package com.hyewon.wiseowl_backend.domain;

import com.hyewon.wiseowl_backend.domain.course.entity.Major;
import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorRequirement;
import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;
import com.hyewon.wiseowl_backend.domain.requirement.repository.MajorRequirementRepository;
import com.hyewon.wiseowl_backend.domain.requirement.service.MajorRequirementQueryService;
import com.hyewon.wiseowl_backend.fixture.CourseFixture;
import com.hyewon.wiseowl_backend.fixture.RequirementFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class MajorRequirementQueryServiceTest {
    @InjectMocks MajorRequirementQueryService majorRequirementQueryService;
    @Mock MajorRequirementRepository majorRequirementRepository;

    private Major major;
    private MajorRequirement majorRequirement;

    @BeforeEach
    void setUp() {
        major = CourseFixture.aDefaultMajor();
        majorRequirement = RequirementFixture.aMajorRequirement(major);
    }

    @Test
    @DisplayName("getMajorName - should return major name")
    void getApplicableRequirements_shouldSucceed() {
        // given
        given(majorRequirementRepository.findApplicable(1L, MajorType.PRIMARY, 2021)).willReturn(
                List.of(majorRequirement));

        // when
        List<MajorRequirement> applicableRequirements = majorRequirementQueryService.getApplicableRequirements(1L, MajorType.PRIMARY, 2021);

        // then
        assertThat(applicableRequirements).hasSize(1);
        assertThat(applicableRequirements.get(0)).isEqualTo(majorRequirement);
    }
}
