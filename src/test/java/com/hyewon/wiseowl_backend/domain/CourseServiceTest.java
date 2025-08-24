package com.hyewon.wiseowl_backend.domain;

import com.hyewon.wiseowl_backend.domain.course.dto.CollegeWithMajorsResponse;
import com.hyewon.wiseowl_backend.domain.course.dto.CourseCategoryResponse;
import com.hyewon.wiseowl_backend.domain.course.dto.CourseOfferingResponse;
import com.hyewon.wiseowl_backend.domain.course.entity.*;
import com.hyewon.wiseowl_backend.domain.course.repository.CourseOfferingRepository;
import com.hyewon.wiseowl_backend.domain.course.repository.MajorRepository;
import com.hyewon.wiseowl_backend.domain.course.service.CourseService;
import com.hyewon.wiseowl_backend.fixture.CourseFixture;
import com.hyewon.wiseowl_backend.global.exception.CourseNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {
    @Mock private CourseOfferingRepository courseOfferingRepository;
    @Mock private MajorRepository majorRepository;
    @InjectMocks private CourseService courseService;

    private Major major;
    private Major major2;
    private College college;
    private LiberalCategory liberal;
    private Course course;
    private CourseOffering courseOffering;
    private Course liberalCourse;
    private CourseOffering liberalCourseOffering;

    @BeforeEach
    void setUp() throws Exception {
        college = CourseFixture.aCollege();
        major = CourseFixture.aMajor1(college);
        major2 = CourseFixture.aMajor2(college);
        liberal = CourseFixture.aLiberalCategory();
        course = CourseFixture.aMajorCourse(major);
        courseOffering = CourseFixture.aMajorCourseOffering(course);
        liberalCourse = CourseFixture.aLiberalCourse();
        liberalCourseOffering =CourseFixture.aLiberalCourseOffering(liberalCourse);
    }

    @Test
    @DisplayName("getCourseCategoriesBySemester - returns combined majors and liberals")
    void shouldReturnCourseCategories_whenCourseOfferingsExistForSemester() {
        // given
        Long semesterId = 1L;
        given(courseOfferingRepository.findDistinctMajorsBySemesterId(semesterId))
                .willReturn(List.of(major));
        given(courseOfferingRepository.findDistinctLiberalCategoriesBySemester(semesterId))
                .willReturn(List.of(liberal));

        // when
        List<CourseCategoryResponse> result = courseService.getCourseCategoriesBySemester(semesterId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting("name")
                .containsExactlyInAnyOrder("컴퓨터공학과", "언어와문학");
    }

    @Test
    @DisplayName("getCourseCategoriesBySemester - throws exception when both are empty")
    void shouldThrowException_whenNoCourseOfferingExistForSemester() {
        // given
        Long semesterId = 2L;
        given(courseOfferingRepository.findDistinctMajorsBySemesterId(semesterId))
                .willReturn(List.of());
        given(courseOfferingRepository.findDistinctLiberalCategoriesBySemester(semesterId))
                .willReturn(List.of());

        // when & then
        assertThatThrownBy(() -> courseService.getCourseCategoriesBySemester(semesterId))
                .isInstanceOf(CourseNotFoundException.class)
                .hasMessageContaining("No course categories found for semesterId");
    }

    @Test
    @DisplayName("getCourseOfferingsBySemester - success case")
    void shouldReturnCourseOfferings_whenCourseOfferingsExistForSemester() {
        // given
        Long semesterId = 1L;
        CourseOfferingResponse response1 = CourseOfferingResponse.from(courseOffering, null);
        CourseOfferingResponse response2 = CourseOfferingResponse.from(liberalCourseOffering, 1L);
        given(courseOfferingRepository.findCourseOfferingsBySemester(semesterId))
                .willReturn(List.of(response1, response2));

        // when
        List<CourseOfferingResponse> result = courseService.getCourseOfferingsBySemester(semesterId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting("courseName").containsExactlyInAnyOrder("자료구조", "글쓰기");
    }

    @Test
    @DisplayName("getCollegesWithMajors – groups majors by college")
    void shouldReturnCollegesWithMajors_whenMajorExists() {
        // given
        given(majorRepository.findAllWithCollege())
                .willReturn(List.of(major, major2));

        // when
        List<CollegeWithMajorsResponse> result =
                courseService.getCollegesWithMajors();

        // then
        CollegeWithMajorsResponse dto = result.get(0);

        assertThat(result).hasSize(1);
        assertThat(dto.collegeId()).isEqualTo(1L);
        assertThat(dto.collegeName()).isEqualTo("공과대학");
        assertThat(dto.majors())
                .extracting("majorName")
                .containsExactlyInAnyOrder("컴퓨터공학과", "전기전자공학과");
    }

    @Test
    @DisplayName("getCollegesWithMajors – returns empty list when no majors exist")
    void shouldReturnEmptyList_whenNoMajorExists() {
        // given
        given(majorRepository.findAllWithCollege())
                .willReturn(List.of());

        // when
        List<CollegeWithMajorsResponse> result = courseService.getCollegesWithMajors();

        // then
        assertThat(result).isEmpty();
    }
}
