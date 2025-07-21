package com.hyewon.wiseowl_backend.integration;

import com.hyewon.wiseowl_backend.domain.course.entity.Course;
import com.hyewon.wiseowl_backend.domain.course.entity.CourseOffering;
import com.hyewon.wiseowl_backend.domain.course.entity.Semester;
import com.hyewon.wiseowl_backend.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CourseControllerIT extends AbstractIntegrationTest{
    @Test
    @DisplayName("GET /api/courses/course-categories- returns course categories grouped by semester")
    void getCourseCategories_success() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getId());

        Semester semester = testDataLoader.getTestSemester();

        mockMvc.perform(get("/api/courses/course-categories")
                        .param("semesterId", String.valueOf(semester.getId()))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseCategories").isArray());

    }

    @Test
    @DisplayName("GET /api/courses/course-categories - should return 404 if course does not exist")
    void getCourseCategories_courseNotFound() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getId());


        mockMvc.perform(get("/api/courses/course-categories")
                        .param("semesterId", String.valueOf(999L))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("COURSE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").exists());

    }

    @Test
    @DisplayName("GET /api/courses/offerings - returns course offerings grouped by semester")
    void getOfferings_success() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getId());

        Semester semester = testDataLoader.getTestSemester();

        mockMvc.perform(get("/api/courses/offerings")
                        .param("semesterId", String.valueOf(semester.getId()))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].majorId").exists())
                .andExpect(jsonPath("$[0].courseName").value("자료구조"));

    }

    @Test
    @DisplayName("GET /api/courses/offerings - should return 404 if course does not exist")
    void getOfferings_courseNotFound() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getId());


        mockMvc.perform(get("/api/courses/offerings")
                        .param("semesterId", String.valueOf(999L))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("COURSE_OFFERING_NOT_FOUND"))
                .andExpect(jsonPath("$.message").exists());

    }

    @Test
    @DisplayName("GET /api/courses/offerings - should return 404 if liberal category does not exist")
    void getOfferings_liberalCategoryNotFound() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getId());
        Semester semester = testDataLoader.getTestSemester();

        Course liberalCourse = Course.builder()
                .name("사회문명")
                .courseCodePrefix("CD234")
                .build();
        courseRepository.save(liberalCourse);

        CourseOffering offering = CourseOffering.builder()
                .course(liberalCourse)
                .semester(semester)
                .professor("홍길동")
                .build();

        courseOfferingRepository.save(offering);


        mockMvc.perform(get("/api/courses/offerings")
                        .param("semesterId", String.valueOf(semester.getId()))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("LIBERAL_CATEGORY_NOT_FOUND"))
                .andExpect(jsonPath("$.message").exists());

    }

    @Test
    @DisplayName("GET /api/courses/colleges-with-majors - returns majors grouped by college ")
    void getCollegesWithMajors_success() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getId());


        mockMvc.perform(get("/api/courses/colleges-with-majors")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].collegeId").exists())
                .andExpect(jsonPath("$[0].collegeName").value("공과대학"));

    }

}
