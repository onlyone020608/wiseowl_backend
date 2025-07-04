package com.hyewon.wiseowl_backend.domain;

import com.hyewon.wiseowl_backend.domain.course.dto.CollegeWithMajorsDto;
import com.hyewon.wiseowl_backend.domain.course.repository.MajorRepository;
import com.hyewon.wiseowl_backend.domain.course.service.CourseService;
import com.hyewon.wiseowl_backend.domain.course.dto.CourseCategoryDto;
import com.hyewon.wiseowl_backend.domain.course.dto.CourseOfferingDto;
import com.hyewon.wiseowl_backend.domain.course.entity.*;
import com.hyewon.wiseowl_backend.domain.course.repository.CourseOfferingRepository;
import com.hyewon.wiseowl_backend.domain.course.repository.LiberalCategoryRepository;
import com.hyewon.wiseowl_backend.global.exception.CourseNotFoundException;
import com.hyewon.wiseowl_backend.global.exception.LiberalCategoryNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    private Room room;
    private Course course;
    private CourseOffering offering;
    private Course liberalCourse;
    private CourseOffering liberalOffering;


    @BeforeEach
    void setUp() throws Exception {
        // College
        Constructor<College> collegeCtor = College.class.getDeclaredConstructor();
        collegeCtor.setAccessible(true);
        college = collegeCtor.newInstance();
        ReflectionTestUtils.setField(college, "id", 1L);
        ReflectionTestUtils.setField(college, "name", "공과대학");

        // Major A
        Constructor<Major> majorCtor = Major.class.getDeclaredConstructor();
        majorCtor.setAccessible(true);
        major1 = majorCtor.newInstance();
        ReflectionTestUtils.setField(major1, "id", 10L);
        ReflectionTestUtils.setField(major1, "name", "컴퓨터공학과");
        ReflectionTestUtils.setField(major1, "college", college);

        // Major B
        major2 = majorCtor.newInstance();
        ReflectionTestUtils.setField(major2, "id", 11L);
        ReflectionTestUtils.setField(major2, "name", "전기전자공학과");
        ReflectionTestUtils.setField(major2, "college", college);

        //LiberalCategory
        Constructor<LiberalCategory> liberalCtor = LiberalCategory.class.getDeclaredConstructor();
        liberalCtor.setAccessible(true);
        liberal = liberalCtor.newInstance();
        ReflectionTestUtils.setField(liberal, "id", 1L);
        ReflectionTestUtils.setField(liberal, "name", "언어와문학");

        // Building
        Constructor<Building> buildingCtor = Building.class.getDeclaredConstructor();
        buildingCtor.setAccessible(true);
        Building building = buildingCtor.newInstance();
        ReflectionTestUtils.setField(building, "id", 1L);
        ReflectionTestUtils.setField(building, "name", "백년관");

        // Room
        Constructor<Room> roomCtor = Room.class.getDeclaredConstructor();
        roomCtor.setAccessible(true);
        room = roomCtor.newInstance();
        ReflectionTestUtils.setField(room, "id", 1L);
        ReflectionTestUtils.setField(room, "building", building);
        ReflectionTestUtils.setField(room, "roomNumber", "101");

        // 전공 Course
        Constructor<Course> courseCtor = Course.class.getDeclaredConstructor();
        courseCtor.setAccessible(true);
        course = courseCtor.newInstance();
        ReflectionTestUtils.setField(course, "id", 10L);
        ReflectionTestUtils.setField(course, "courseCodePrefix", "CSE");
        ReflectionTestUtils.setField(course, "credit", 3);
        ReflectionTestUtils.setField(course, "courseType", CourseType.MAJOR);
        ReflectionTestUtils.setField(course, "name", "자료구조");
        ReflectionTestUtils.setField(course, "major", major1);

        // 전공 CourseOffering
        Constructor<CourseOffering> offeringCtor = CourseOffering.class.getDeclaredConstructor();
        offeringCtor.setAccessible(true);
        offering = offeringCtor.newInstance();
        ReflectionTestUtils.setField(offering, "id", 100L);
        ReflectionTestUtils.setField(offering, "course", course);
        ReflectionTestUtils.setField(offering, "room", room);
        ReflectionTestUtils.setField(offering, "courseCode", "CSE101");

        // 교양 Course
        Constructor<Course> liberalCourseCtor = Course.class.getDeclaredConstructor();
        liberalCourseCtor.setAccessible(true);
        liberalCourse = liberalCourseCtor.newInstance();
        ReflectionTestUtils.setField(liberalCourse, "id", 20L);
        ReflectionTestUtils.setField(liberalCourse, "courseCodePrefix", "GEN");
        ReflectionTestUtils.setField(liberalCourse, "credit", 2);
        ReflectionTestUtils.setField(liberalCourse, "courseType", CourseType.GENERAL);
        ReflectionTestUtils.setField(liberalCourse, "name", "글쓰기");

        // 교양 CourseOffering
        Constructor<CourseOffering> liberalOfferingCtor = CourseOffering.class.getDeclaredConstructor();
        liberalOfferingCtor.setAccessible(true);
        liberalOffering = liberalOfferingCtor.newInstance();
        ReflectionTestUtils.setField(liberalOffering, "id", 200L);
        ReflectionTestUtils.setField(liberalOffering, "course", liberalCourse);
        ReflectionTestUtils.setField(liberalOffering, "room", room);
        ReflectionTestUtils.setField(liberalOffering, "courseCode", "GEN101");

    }

    @Test
    @DisplayName("getCourseCategoriesBySemester - returns combined majors and liberals")
    void getCourseCategories_success(){
        // given
        Long semesterId = 1L;
        given(courseOfferingRepository.findDistinctMajorsBySemesterId(semesterId))
                .willReturn(List.of(major1));
        given(courseOfferingRepository.findDistinctLiberalCategoriesBySemester(semesterId))
                .willReturn(List.of(liberal));

        // when & then
        List<CourseCategoryDto> result = courseService.getCourseCategoriesBySemester(semesterId);

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
                .willReturn(List.of(offering, liberalOffering));

        given(liberalCategoryRepository.findByCourse(liberalCourse))
                .willReturn(Optional.of(liberal));

        // when
        List<CourseOfferingDto> result = courseService.getCourseOfferingsBySemester(semesterId);

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
        assertThrows(CourseNotFoundException.class, () -> {
            courseService.getCourseOfferingsBySemester(1L);
        });
    }

    @Test
    @DisplayName("getCourseOfferingsBySemester - liberal category not found")
    void getCourseOfferings_shouldThrow_whenLiberalNotFound() {
        // given
        given(courseOfferingRepository.findAllBySemesterId(1L))
                .willReturn(List.of(liberalOffering));

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
        assertThat(result).hasSize(1);
        CollegeWithMajorsDto dto = result.get(0);

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
