package com.hyewon.wiseowl_backend.domain;

import com.hyewon.wiseowl_backend.domain.course.entity.Major;
import com.hyewon.wiseowl_backend.domain.requirement.entity.CreditRequirement;
import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;
import com.hyewon.wiseowl_backend.domain.requirement.entity.Track;
import com.hyewon.wiseowl_backend.domain.requirement.repository.CreditRequirementRepository;
import com.hyewon.wiseowl_backend.domain.requirement.service.CreditRequirementQueryService;
import com.hyewon.wiseowl_backend.global.exception.CreditRequirementNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
               .build();
    }

    @Test
    @DisplayName("getMajorName - should return credit requirement")
    void getCreditRequirements_shouldSucceed() {
        // given
        given(creditRequirementRepository.findAllByMajorIdAndMajorTypeAndTrack(1L, MajorType.PRIMARY, Track.PRIMARY_WITH_DOUBLE)).willReturn(
                List.of(creditRequirement));

        // when
        List<CreditRequirement> creditRequirements = creditRequirementQueryService.getCreditRequirements(1L, MajorType.PRIMARY, Track.PRIMARY_WITH_DOUBLE);

        // then
        assertThat(creditRequirements).isEqualTo(List.of(creditRequirement));
    }

    @Test
    @DisplayName("getMajorName - should throw CreditRequirementNotFoundException when credit requirement does not exist")
    void getCreditRequirements_shouldThrowException_whenMajorNotFound() {
        // given
        given(creditRequirementRepository.findAllByMajorIdAndMajorTypeAndTrack(999L, MajorType.PRIMARY, Track.PRIMARY_WITH_DOUBLE)).willReturn(
                List.of()
        );

        // when & then
        assertThrows(CreditRequirementNotFoundException.class,
                () ->  creditRequirementQueryService.getCreditRequirements(999L, MajorType.PRIMARY, Track.PRIMARY_WITH_DOUBLE));
    }
}
