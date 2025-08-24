package com.hyewon.wiseowl_backend.domain;

import com.hyewon.wiseowl_backend.domain.course.entity.Major;
import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;
import com.hyewon.wiseowl_backend.domain.requirement.entity.Track;
import com.hyewon.wiseowl_backend.domain.requirement.repository.CreditRequirementRepository;
import com.hyewon.wiseowl_backend.domain.requirement.service.CreditRequirementQueryService;
import com.hyewon.wiseowl_backend.fixture.CourseFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class CreditRequirementQueryServiceTest {
    @InjectMocks CreditRequirementQueryService creditRequirementQueryService;
    @Mock CreditRequirementRepository creditRequirementRepository;

    private Major major;

    @BeforeEach
    void setUp() {
        major = CourseFixture.aMajor();
    }

    @Test
    @DisplayName("sumRequiredCredits - should return required credit")
    void shouldReturnRequiredCreditsSum_whenValidInputsProvided() {
        // given
        given(creditRequirementRepository.sumRequiredCredits(major, MajorType.PRIMARY, Track.PRIMARY_WITH_DOUBLE, 2024)).willReturn(130);

        // when
        int creditRequirements = creditRequirementQueryService.sumRequiredCredits(major, MajorType.PRIMARY, Track.PRIMARY_WITH_DOUBLE, 2024);

        // then
        assertThat(creditRequirements).isEqualTo(130);
    }
}
