package com.hyewon.wiseowl_backend.domain;

import com.hyewon.wiseowl_backend.domain.course.entity.Major;
import com.hyewon.wiseowl_backend.domain.requirement.entity.CreditRequirement;
import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;
import com.hyewon.wiseowl_backend.domain.requirement.entity.Track;
import com.hyewon.wiseowl_backend.domain.requirement.repository.CreditRequirementRepository;
import com.hyewon.wiseowl_backend.domain.requirement.service.CreditRequirementQueryService;
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
    private CreditRequirement creditRequirement;

    @BeforeEach
    void setUp() {
        major = Major.builder()
                .id(1L)
                .build();
       creditRequirement = CreditRequirement.builder()
               .major(major)
               .majorType(MajorType.PRIMARY)
               .track(Track.PRIMARY_WITH_DOUBLE)
               .requiredCredits(130)
               .build();
    }

    @Test
    @DisplayName("sumRequiredCredits - should return required credit")
    void sumRequiredCredits_shouldSucceed() {
        // given
        given(creditRequirementRepository.sumRequiredCredits(major, MajorType.PRIMARY, Track.PRIMARY_WITH_DOUBLE, 2024)).willReturn(130);

        // when
        int creditRequirements = creditRequirementQueryService.sumRequiredCredits(major, MajorType.PRIMARY, Track.PRIMARY_WITH_DOUBLE, 2024);

        // then
        assertThat(creditRequirements).isEqualTo(130);
    }
}
