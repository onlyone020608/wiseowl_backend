package com.hyewon.wiseowl_backend.domain;

import com.hyewon.wiseowl_backend.domain.course.dto.CollegeWithMajorsDto;
import com.hyewon.wiseowl_backend.domain.course.dto.CourseCategoryResponse;
import com.hyewon.wiseowl_backend.domain.course.dto.CourseOfferingResponse;
import com.hyewon.wiseowl_backend.domain.course.entity.*;
import com.hyewon.wiseowl_backend.domain.course.repository.CourseOfferingRepository;
import com.hyewon.wiseowl_backend.domain.course.repository.LiberalCategoryRepository;
import com.hyewon.wiseowl_backend.domain.course.repository.MajorRepository;
import com.hyewon.wiseowl_backend.domain.course.service.CourseService;
import com.hyewon.wiseowl_backend.global.exception.CourseNotFoundException;
import com.hyewon.wiseowl_backend.global.exception.CourseOfferingNotFoundException;
import com.hyewon.wiseowl_backend.global.exception.LiberalCategoryNotFoundException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {
    @Mock private CourseOfferingRepository courseOfferingRepository;
    @Mock private LiberalCategoryRepository liberalCategoryRepository;
    @Mock private MajorRepository majorRepository;
    @InjectMocks private CourseService courseService;

    private Major major1;
    private Major major2;
    private College college;
    private LiberalCategory liberal;
    private Course course;
    private CourseOffering courseOffering;
    private Course liberalCourse;
    private CourseOffering liberalCourseOffering;

    @BeforeEach
    void setUp() throws Exception {
        college = College.builder()
                .id(1L)
                .name("공과대학")
                .build();
        major1 = Major.builder()
                .id(10L)
                .name("컴퓨터공학과")
                .college(college)
                .build();
        major2 = Major.builder()
                .id(11L)
                .name("전기전자공학과")
                .college(college)
                .build();
        liberal = LiberalCategory.builder()
                .id(1L)
                .name("언어와문학")
                .build();
        course = Course.builder()
                .id(10L)
                .courseCodePrefix("CSE")
                .credit(3)
                .courseType(CourseType.MAJOR)
                .name("자료구조")
                .major(major1)
                .build();
        courseOffering = CourseOffering.builder()
                .id(100L)
                .course(course)
                .room("0409")
                .courseCode("CSE101")
                .build();
        liberalCourse = Course.builder()
                .id(20L)
                .courseCodePrefix("GEN")
                .credit(2)
                .courseType(CourseType.GENERAL)
                .name("글쓰기")
                .build();
        liberalCourseOffering = CourseOffering.builder()
                .id(200L)
                .course(liberalCourse)
                .room("0409")
                .courseCode("GEN101")
                .build();
    }

    @Test
    @DisplayName("getCourseCategoriesBySemester - returns combined majors and liberals")
    void getCourseCategories_success() {
        // given
        Long semesterId = 1L;
        given(courseOfferingRepository.findDistinctMajorsBySemesterId(semesterId))
                .willReturn(List.of(major1));
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
    void getCourseCategories_fail_whenEmpty() {
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
    void getCourseOfferings_success() {
        // given
        Long semesterId = 1L;
        given(courseOfferingRepository.findAllBySemesterId(semesterId))
                .willReturn(List.of(courseOffering, liberalCourseOffering));
        given(liberalCategoryRepository.findByCourse(liberalCourse))
                .willReturn(Optional.of(liberal));

        // when
        List<CourseOfferingResponse> result = courseService.getCourseOfferingsBySemester(semesterId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting("courseName").containsExactlyInAnyOrder("자료구조", "글쓰기");
    }

    @Test
    @DisplayName("getCourseOfferingsBySemester - no courses found")
    void getCourseOfferings_shouldThrow_whenEmpty() {
        // given
        Long semesterId = 1L;
        given(courseOfferingRepository.findAllBySemesterId(semesterId))
                .willReturn(List.of());

        // when & then
        assertThrows(CourseOfferingNotFoundException.class, () -> {
            courseService.getCourseOfferingsBySemester(1L);
        });
    }

    @Test
    @DisplayName("getCourseOfferingsBySemester - liberal category not found")
    void getCourseOfferings_shouldThrow_whenLiberalNotFound() {
        // given
        given(courseOfferingRepository.findAllBySemesterId(1L))
                .willReturn(List.of(liberalCourseOffering));
        given(liberalCategoryRepository.findByCourse(liberalCourse))
                .willReturn(Optional.empty());

        // when & then
        assertThrows(LiberalCategoryNotFoundException.class, () -> {
            courseService.getCourseOfferingsBySemester(1L);
        });
    }

    @Test
    @DisplayName("getCollegesWithMajors – groups majors by college")
    void getCollegesWithMajors_success() {
        // given
        given(majorRepository.findAllWithCollege())
                .willReturn(List.of(major1, major2));

        // when
        List<CollegeWithMajorsDto> result =
                courseService.getCollegesWithMajors();

        // then
        CollegeWithMajorsDto dto = result.get(0);

        assertThat(result).hasSize(1);
        assertThat(dto.collegeId()).isEqualTo(1L);
        assertThat(dto.collegeName()).isEqualTo("공과대학");
        assertThat(dto.majors())
                .extracting("majorName")
                .containsExactlyInAnyOrder("컴퓨터공학과", "전기전자공학과");
    }

    @Test
    @DisplayName("getCollegesWithMajors – returns empty list when no majors exist")
    void getCollegesWithMajors_emptyList() {
        // given
        given(majorRepository.findAllWithCollege())
                .willReturn(List.of());

        // when
        List<CollegeWithMajorsDto> result = courseService.getCollegesWithMajors();

        // then
        assertThat(result).isEmpty();
    }
}
