package com.hyewon.wiseowl_backend.integration;

import com.hyewon.wiseowl_backend.domain.course.entity.CourseOffering;
import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;
import com.hyewon.wiseowl_backend.domain.user.dto.*;
import com.hyewon.wiseowl_backend.domain.user.entity.Grade;
import com.hyewon.wiseowl_backend.domain.user.entity.User;
import com.hyewon.wiseowl_backend.domain.user.entity.UserCompletedCourse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerIT extends AbstractIntegrationTest{
    @Test
    @DisplayName("POST /api/users/me/profile - should update user profile")
    void updateProfile_success() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getId());


        ProfileUpdateRequest request = new ProfileUpdateRequest(
                "testName",
                2021,
                List.of(new UserMajorRequest(
                        1L, MajorType.PRIMARY
                ))
        );


        mockMvc.perform(post("/api/users/me/profile")
                        .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());


    }

    @Test
    @DisplayName("POST /api/users/me/profile -  should return 404 when no profile exists")
    void updateProfile_profileNotFound() throws Exception {
        User user = userRepository.save(User.builder()
                .email("test@example.com")
                .password("encoded-password")
                .username("Tester")
                .build());
        String token = jwtProvider.generateAccessToken(user.getId());


        ProfileUpdateRequest request = new ProfileUpdateRequest(
                "testName",
                2021,
                List.of(new UserMajorRequest(
                        1L, MajorType.PRIMARY
                ))
        );


        mockMvc.perform(post("/api/users/me/profile")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("PROFILE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").exists());


    }

    @Test
    @DisplayName("POST /api/users/me/profile -  should return 404 when no major exists")
    void updateProfile_majorNotFound() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getId());


        ProfileUpdateRequest request = new ProfileUpdateRequest(
                "testName",
                2021,
                List.of(new UserMajorRequest(
                        999L, MajorType.PRIMARY
                ))
        );


        mockMvc.perform(post("/api/users/me/profile")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("MAJOR_NOT_FOUND"))
                .andExpect(jsonPath("$.message").exists());


    }

    @Test
    @DisplayName("POST /api/users/me/completed-courses - insert completed courses for the users")
    void insertCompletedCourses_success() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getId());


        CompletedCourseInsertRequest request = new CompletedCourseInsertRequest(
             List.of( new CompletedCourseInsertItem(1L, Grade.A, false))
        );


        mockMvc.perform(post("/api/users/me/completed-courses")
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());


    }

    @Test
    @DisplayName("POST /api/users/me/completed-courses - should return 409 when user completed course already exists")
    void insertCompletedCourses_userCompletedCourseAlreadyExists() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getId());
        CourseOffering offering = courseOfferingRepository.findById(1L).orElseThrow(() -> new IllegalStateException("Test setup failed: courseOffering not found"));

        userCompletedCourseRepository.save(UserCompletedCourse.builder()
                        .user(user)
                        .retake(false)
                        .courseOffering(offering)
                .build());


        CompletedCourseInsertRequest request = new CompletedCourseInsertRequest(
                List.of( new CompletedCourseInsertItem(1L, Grade.A, false))
        );


        mockMvc.perform(post("/api/users/me/completed-courses")
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("COMPLETED_COURSE_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.message").exists());


    }

    @Test
    @DisplayName("POST /api/users/me/completed-courses - should return 404 when no course offering exists")
    void insertCompletedCourses_courseOfferingNotfound() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getId());

        CompletedCourseInsertRequest request = new CompletedCourseInsertRequest(
                List.of( new CompletedCourseInsertItem(999L, Grade.A, false))
        );


        mockMvc.perform(post("/api/users/me/completed-courses")
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("COURSE_OFFERING_NOT_FOUND"))
                .andExpect(jsonPath("$.message").exists());


    }

    @Test
    @DisplayName("GET /api/users/me/graduation-requirements - returns graduation requirements for user")
    void getGraduationRequirements_success() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getId());


        mockMvc.perform(get("/api/users/me/graduation-requirements")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));


    }

    @Test
    @DisplayName("GET /api/users/me/graduation-requirements - should return 404 when no user requirement status exists")
    @Sql(statements = "DELETE FROM user_requirement_status", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getGraduationRequirements_userRequirementStatusNotFound() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getId());


        mockMvc.perform(get("/api/users/me/graduation-requirements")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("USER_GRADUATION_STATUS_NOT_FOUND"))
                .andExpect(jsonPath("$.message").exists());
;

    }
}
