package com.hyewon.wiseowl_backend.domain;

import com.hyewon.wiseowl_backend.domain.course.entity.Course;
import com.hyewon.wiseowl_backend.domain.course.entity.CourseOffering;
import com.hyewon.wiseowl_backend.domain.course.entity.CourseType;
import com.hyewon.wiseowl_backend.domain.course.repository.CourseOfferingRepository;
import com.hyewon.wiseowl_backend.domain.course.service.CourseOfferingQueryService;
import com.hyewon.wiseowl_backend.global.exception.CourseOfferingNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class CourseOfferingQueryServiceTest {
    @InjectMocks
    CourseOfferingQueryService courseOfferingQueryService;

    @Mock
    CourseOfferingRepository courseOfferingRepository;

    private CourseOffering offering;
    private Course course;
    @BeforeEach
    void setUp() {
        course = Course.builder()
                .name("자료구조")
                .courseCodePrefix("V41006")
                .credit(3)
                .courseType(CourseType.MAJOR)
                .build();

        offering = CourseOffering.builder()
                .id(1L)
                .course(course)
                .build();

    }

    @Test
    @DisplayName("getCourseOffering - should return course offering")
    void getCourseOffering_shouldSucceed() {
        // given
        Long courseOfferingId = 1L;
        given(courseOfferingRepository.findById(courseOfferingId)).willReturn(
                Optional.of(offering));

        // when
        CourseOffering courseOffering = courseOfferingQueryService.getCourseOffering(courseOfferingId);

        // then
        assertThat(courseOffering).isEqualTo(offering);
    }

    @Test
    @DisplayName("getCourseOffering - should throw CourseOfferingNotFoundException when course offering does not exist")
    void getCourseOffering_shouldThrowException_whenCourseOfferingNotFound() {
        // given
        Long courseOfferingId = 999L;
        given(courseOfferingRepository.findById(courseOfferingId)).willReturn(
                Optional.empty());

        // when & then
        assertThrows(CourseOfferingNotFoundException.class,
                () ->  courseOfferingQueryService.getCourseOffering(courseOfferingId));
    }
}
