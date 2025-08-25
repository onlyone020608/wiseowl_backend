package com.hyewon.wiseowl_backend.domain;

import com.hyewon.wiseowl_backend.domain.course.entity.Major;
import com.hyewon.wiseowl_backend.domain.requirement.entity.RequiredLiberalCategory;
import com.hyewon.wiseowl_backend.domain.requirement.repository.RequiredLiberalCategoryRepository;
import com.hyewon.wiseowl_backend.domain.requirement.service.RequiredLiberalCategoryQueryService;
import com.hyewon.wiseowl_backend.fixture.CourseFixture;
import com.hyewon.wiseowl_backend.fixture.RequirementFixture;
import com.hyewon.wiseowl_backend.global.exception.RequiredLiberalCategoryNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class RequiredLiberalCategoryQueryServiceTest {
    @InjectMocks RequiredLiberalCategoryQueryService requiredLiberalCategoryQueryService;
    @Mock RequiredLiberalCategoryRepository requiredLiberalCategoryRepository;

    private RequiredLiberalCategory requiredLiberalCategory;
    private Major major;

    @BeforeEach
    void setUp() {
        major = CourseFixture.aDefaultMajor();
        requiredLiberalCategory = RequirementFixture.aRequiredLiberalCategory(major);
    }

    @Test
    @DisplayName("returns applicable required liberal categories when major and entrance year are provided")
    void shouldReturnApplicableLiberalCategories_whenMajorAndYearProvided() {
        // given
        given(requiredLiberalCategoryRepository.findApplicableLiberalCategories(1L, 2021)).willReturn(
                List.of(requiredLiberalCategory));

        // when
        List<RequiredLiberalCategory> applicableLiberalCategories = requiredLiberalCategoryQueryService.getApplicableLiberalCategories(1L, 2021);

        // then
        assertThat(applicableLiberalCategories).hasSize(1);
        assertThat(applicableLiberalCategories.get(0)).isEqualTo(requiredLiberalCategory);
    }

    @Test
    @DisplayName("returns required liberal category when id exists")
    void shouldReturnRequiredLiberalCategory_whenIdExist() {
        // given
        given(requiredLiberalCategoryRepository.findById(2L)).willReturn(
                Optional.of(requiredLiberalCategory));

        // when
        RequiredLiberalCategory result = requiredLiberalCategoryQueryService.getRequiredLiberalCategory(2L);

        // then
        assertThat(result).isEqualTo(requiredLiberalCategory);
    }

    @Test
    @DisplayName("throws RequiredLiberalCategoryNotFoundException when id does not exist")
    void shouldThrowException_whenRequiredLiberalCategoryNotFound() {
        // given
        given(requiredLiberalCategoryRepository.findById(999L)).willReturn(
                Optional.empty());

        // when & then
        assertThrows(RequiredLiberalCategoryNotFoundException.class,
                () ->  requiredLiberalCategoryQueryService.getRequiredLiberalCategory(999L));
    }
}
