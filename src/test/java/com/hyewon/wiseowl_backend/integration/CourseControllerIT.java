package com.hyewon.wiseowl_backend.integration;

import com.hyewon.wiseowl_backend.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CourseControllerIT extends AbstractIntegrationTest {
    @Test
    @DisplayName("GET /api/courses/course-categories- returns course categories for a valid semesterr")
    void getCourseCategories_withValidSemesterId_returnsCourseCategories() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getEmail());

        Long semesterId = 13L;

        mockMvc.perform(get("/api/courses/course-categories")
                        .param("semesterId", String.valueOf(semesterId))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseCategories").isArray())
                .andExpect(jsonPath("$.courseCategories", hasSize(73)));
    }

    @Test
    @DisplayName("GET /api/courses/course-categories - returns 404 when semester does not exist")
    void getCourseCategories_withInvalidSemesterId_returns404() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getEmail());

        mockMvc.perform(get("/api/courses/course-categories")
                        .param("semesterId", String.valueOf(999L))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("COURSE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("GET /api/courses/offerings - returns course offerings for a valid semester")
    void getCourseOfferings_withValidSemesterId_returnsOfferings() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getEmail());
        Long semesterId = 13L;

        mockMvc.perform(get("/api/courses/offerings")
                        .param("semesterId", String.valueOf(semesterId))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.offerings").isArray())
                .andExpect(jsonPath("$.offerings", hasSize(1479)));
    }

    @Test
    @DisplayName("GET /api/courses/colleges-with-majors - returns colleges with their majors")
    void getCollegesWithMajors_withValidRequest_returnsCollegesAndMajors() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getEmail());

        mockMvc.perform(get("/api/courses/colleges-with-majors")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.colleges").isArray())
                .andExpect(jsonPath("$.colleges", hasSize(14)));
    }
}
