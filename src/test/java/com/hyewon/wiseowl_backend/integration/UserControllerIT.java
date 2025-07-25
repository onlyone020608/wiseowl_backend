package com.hyewon.wiseowl_backend.integration;

import com.hyewon.wiseowl_backend.domain.course.entity.CourseOffering;
import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;
import com.hyewon.wiseowl_backend.domain.user.dto.*;
import com.hyewon.wiseowl_backend.domain.user.entity.Grade;
import com.hyewon.wiseowl_backend.domain.user.entity.SubscriptionType;
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

    @Test
    @DisplayName("PUT /api/users/me/graduation-requirements - should update user requirement status")
    void updateRequirements_success() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getId());


        UserRequirementFulfillmentRequest request = new UserRequirementFulfillmentRequest(
                1L,
                List.of( new RequirementStatusUpdate(1L,true))
        );


        mockMvc.perform(put("/api/users/me/graduation-requirements")
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());


    }

    @Test
    @DisplayName("PUT /api/users/me/graduation-requirements - should return 404 when no user requirement status exists")
    void updateRequirements_userRequirementStatusNotFound() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getId());


        UserRequirementFulfillmentRequest request = new UserRequirementFulfillmentRequest(
                1L,
                List.of( new RequirementStatusUpdate(999L,true))
        );


        mockMvc.perform(put("/api/users/me/graduation-requirements")
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("USER_GRADUATION_STATUS_NOT_FOUND"))
                .andExpect(jsonPath("$.message").exists());


    }

    @Test
    @DisplayName("GET /api/users/me/graduation-info - should returns user graduation info")
    void getMainGraduationInfo_success() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getId());


        mockMvc.perform(get("/api/users/me/graduation-info")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("Tester"));


    }

    @Test
    @DisplayName("GET /api/users/me/graduation-info - should return 404 when no user major exists")
    @Sql(statements = "DELETE FROM user_major", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getMainGraduationInfo_userMajorNotFound() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getId());


        mockMvc.perform(get("/api/users/me/graduation-info")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("USER_MAJOR_NOT_FOUND"))
                .andExpect(jsonPath("$.message").exists());


    }

    @Test
    @DisplayName("GET /api/users/me/graduation-info - should return 404 when no credit requirement exists")
    @Sql(statements = "DELETE FROM credit_requirement", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getMainGraduationInfo_creditRequirementNotFound() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getId());


        mockMvc.perform(get("/api/users/me/graduation-info")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("CREDIT_REQUIREMENT_NOT_FOUND"))
                .andExpect(jsonPath("$.message").exists());


    }

    @Test
    @DisplayName("GET /api/users/me/required-courses -returns user required courses")
    void getMyRequiredCourses_success() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getId());


        mockMvc.perform(get("/api/users/me/required-courses")
                        .header("Authorization", "Bearer " + token)
                .param("majorType", "PRIMARY"))
                .andExpect(status().isOk());


    }

    @Test
    @DisplayName("GET /api/users/me/required-courses -should return 404 when no user required course status exists")
    @Sql(statements = "DELETE FROM user_required_course_status", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getMyRequiredCourses_userRequiredCourseStatusNotFound() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getId());


        mockMvc.perform(get("/api/users/me/required-courses")
                        .header("Authorization", "Bearer " + token)
                        .param("majorType", "PRIMARY"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("USER_REQUIRED_COURSE_STATUS_NOT_FOUND"))
                .andExpect(jsonPath("$.message").exists());


    }

    @Test
    @DisplayName("GET /api/users/me/required-courses -should return 404 when no required major course exists")
    @Sql(statements = "DELETE FROM required_major_course", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getMyRequiredCourses_requiredMajorCourseNotFound() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getId());


        mockMvc.perform(get("/api/users/me/required-courses")
                        .header("Authorization", "Bearer " + token)
                        .param("majorType", "PRIMARY"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("REQUIRED_MAJOR_COURSE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").exists());


    }

    @Test
    @DisplayName("GET /api/users/me/required-courses -should return 404 when no required liberal category exists")
    @Sql(statements = "DELETE FROM required_liberal_category_by_college", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getMyRequiredCourses_requiredLiberalCategoryNotFound() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getId());


        mockMvc.perform(get("/api/users/me/required-courses")
                        .header("Authorization", "Bearer " + token)
                        .param("majorType", "PRIMARY"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("REQUIRED_LIBERAL_CATEGORY_NOT_FOUND"))
                .andExpect(jsonPath("$.message").exists());


    }

    @Test
    @DisplayName("GET /api/users/me/graduation-requirements/statuses -returns user graduation-requirements statuses")
    void getGraduationRequirementStatuses_success() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getId());


        mockMvc.perform(get("/api/users/me/graduation-requirements/statuses")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());


    }

    @Test
    @DisplayName("GET /api/users/me/graduation-requirements/statuses -should return 404 when no user requirement status exists")
    @Sql(statements = "DELETE FROM user_requirement_status", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getGraduationRequirementStatuses_userRequirementStatusNotFound() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getId());


        mockMvc.perform(get("/api/users/me/graduation-requirements/statuses")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("USER_GRADUATION_STATUS_NOT_FOUND"))
                .andExpect(jsonPath("$.message").exists());


    }

    @Test
    @DisplayName("GET /api/users/me/summary -returns user summary")
    void getSummary_success() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getId());


        mockMvc.perform(get("/api/users/me/summary")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("Tester"));


    }

    @Test
    @DisplayName("PATCH /api/users/me/majors - should update user major")
    void updateUserMajor_success() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getId());


        List<UserMajorUpdateRequest> requests = List.of(
                new UserMajorUpdateRequest(MajorType.PRIMARY, 1L)
        );


        mockMvc.perform(patch("/api/users/me/majors")
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(requests))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());


    }

    @Test
    @DisplayName("PATCH /api/users/me/majors - should return 404 when no major exists")
    void updateUserMajor_majorNotFound() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getId());


        List<UserMajorUpdateRequest> requests = List.of(
                new UserMajorUpdateRequest(MajorType.PRIMARY, 999L)
        );


        mockMvc.perform(patch("/api/users/me/majors")
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(requests))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("MAJOR_NOT_FOUND"))
                .andExpect(jsonPath("$.message").exists());


    }

    @Test
    @DisplayName("PATCH /api/users/me/majors/type - should update user major's type")
    void updateUserMajorTypes_success() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getId());


        List<UserMajorTypeUpdateRequest> requests = List.of(
                new UserMajorTypeUpdateRequest(1L, MajorType.DOUBLE)
        );


        mockMvc.perform(patch("/api/users/me/majors/type")
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(requests))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

    }

    @Test
    @DisplayName("PATCH /api/users/me/majors/type - should return 404 when no user major exists")
    void updateUserMajorTypes_userMajorNotFound() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getId());


        List<UserMajorTypeUpdateRequest> requests = List.of(
                new UserMajorTypeUpdateRequest(999L, MajorType.DOUBLE)
        );


        mockMvc.perform(patch("/api/users/me/majors/type")
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(requests))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("USER_MAJOR_NOT_FOUND"))
                .andExpect(jsonPath("$.message").exists());

    }

    @Test
    @DisplayName("PATCH /api/users/me/completed-courses - should update user major's type")
    void updateCompletedCourses_success() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getId());


        List<CompletedCourseUpdateRequest> requests = List.of(
                new CompletedCourseUpdateRequest(1L, Grade.A_PLUS, true)
        );


        mockMvc.perform(patch("/api/users/me/completed-courses")
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(requests))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

    }

    @Test
    @DisplayName("PATCH /api/users/me/completed-courses - should return 404 when no user completed course exists")
    void updateCompletedCourses_userCompletedCourseNotFound() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getId());


        List<CompletedCourseUpdateRequest> requests = List.of(
                new CompletedCourseUpdateRequest(999L, Grade.A_PLUS, true)
        );


        mockMvc.perform(patch("/api/users/me/completed-courses")
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(requests))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("USER_COMPLETED_COURSE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").exists());

    }

    @Test
    @DisplayName("POST /api/users/me/subscriptions - insert subscriptions for user")
    void subscribeOrganizations_success() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getId());


        List<UserSubscriptionRequest> requests = List.of(
                new UserSubscriptionRequest(1L, SubscriptionType.MAJOR)
        );


        mockMvc.perform(post("/api/users/me/subscriptions")
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(requests))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

    }

    @Test
    @DisplayName("PUT /api/users/me/subscriptions - should update user subscriptions")
    void updateUserSubscriptions_success() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getId());


        List<UserSubscriptionRequest> requests = List.of(
                new UserSubscriptionRequest(1L, SubscriptionType.MAJOR)
        );


        mockMvc.perform(put("/api/users/me/subscriptions")
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(requests))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());


    }

    @Test
    @DisplayName("DELETE /api/users/me/ - delete user")
    void deleteUser_success() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getId());


        mockMvc.perform(delete("/api/users/me")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

    }


}
