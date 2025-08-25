package com.hyewon.wiseowl_backend.domain;

import com.hyewon.wiseowl_backend.domain.course.entity.Major;
import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;
import com.hyewon.wiseowl_backend.domain.requirement.entity.RequiredMajorCourse;
import com.hyewon.wiseowl_backend.domain.requirement.repository.RequiredMajorCourseRepository;
import com.hyewon.wiseowl_backend.domain.requirement.service.RequiredMajorCourseQueryService;
import com.hyewon.wiseowl_backend.fixture.CourseFixture;
import com.hyewon.wiseowl_backend.global.exception.RequiredMajorCourseNotFoundException;
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
public class RequiredMajorCourseQueryServiceTest {
    @InjectMocks RequiredMajorCourseQueryService requiredMajorCourseQueryService;
    @Mock RequiredMajorCourseRepository reqMajorCourseRepository;

    private Major major;
    private RequiredMajorCourse requiredMajorCourse;

    @BeforeEach
    void setUp() {
        major = CourseFixture.aDefaultMajor();
        requiredMajorCourse = RequiredMajorCourse.builder()
                .id(2L)
                .major(major)
                .majorType(MajorType.PRIMARY)
                .build();
    }

    @Test
    @DisplayName("returns applicable required major courses when major type and entrance year are provided")
    void shouldReturnApplicableMajorCourses_whenMajorTypeAndEntranceYearProvided() {
        // given
        given(reqMajorCourseRepository.findApplicableMajorCourses(1L, MajorType.PRIMARY, 2021)).willReturn(
                List.of(requiredMajorCourse));

        // when
        List<RequiredMajorCourse> applicableMajorCourses = requiredMajorCourseQueryService.getApplicableMajorCourses(1L, MajorType.PRIMARY, 2021);

        // then
        assertThat(applicableMajorCourses).hasSize(1);
        assertThat(applicableMajorCourses.get(0)).isEqualTo(requiredMajorCourse);
    }

    @Test
    @DisplayName("returns required major course when id exists")
    void shouldReturnRequiredMajorCourse_whenIdExists() {
        // given
        given(reqMajorCourseRepository.findById(2L)).willReturn(
                Optional.of(requiredMajorCourse));

        // when
        RequiredMajorCourse result = requiredMajorCourseQueryService.getRequiredMajorCourse(2L);

        // then
        assertThat(result).isEqualTo(requiredMajorCourse);
    }

    @Test
    @DisplayName("throws RequiredMajorCourseNotFoundException when id does not exist")
    void shouldThrowException_whenRequiredMajorCourseNotFound() {
        // given
        given(reqMajorCourseRepository.findById(999L)).willReturn(
                Optional.empty());

        // when & then
        assertThrows(RequiredMajorCourseNotFoundException.class,
                () ->  requiredMajorCourseQueryService.getRequiredMajorCourse(999L));
    }
}
