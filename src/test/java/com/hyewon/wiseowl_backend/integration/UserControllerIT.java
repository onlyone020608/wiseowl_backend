package com.hyewon.wiseowl_backend.integration;

import com.hyewon.wiseowl_backend.domain.course.entity.CourseOffering;
import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;
import com.hyewon.wiseowl_backend.domain.requirement.entity.Track;
import com.hyewon.wiseowl_backend.domain.user.dto.*;
import com.hyewon.wiseowl_backend.domain.user.entity.*;
import com.hyewon.wiseowl_backend.domain.user.repository.UserMajorRepository;
import com.hyewon.wiseowl_backend.domain.user.repository.UserRequirementStatusRepository;
import com.hyewon.wiseowl_backend.global.exception.UserCompletedCourseNotFoundException;
import com.hyewon.wiseowl_backend.global.exception.UserMajorNotFoundException;
import com.hyewon.wiseowl_backend.global.exception.UserRequirementStatusNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerIT extends AbstractIntegrationTest {
    @Autowired
    private UserRequirementStatusRepository userRequirementStatusRepository;
    @Autowired
    private UserMajorRepository userMajorRepository;

    @Test
    @DisplayName("POST /api/users/me/profile - updates user profile")
    void updateProfile_withValidRequest_updatesUserProfile() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getEmail());

        ProfileUpdateRequest request = new ProfileUpdateRequest(
                "testName",
                2021,
                List.of(new UserMajorRequest(
                        1L, MajorType.PRIMARY
                )),
                Track.PRIMARY_WITH_DOUBLE
        );

        mockMvc.perform(post("/api/users/me/profile")
                        .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/users/me/profile - returns 404 when profile does not exist")
    @Sql(statements = "DELETE FROM profile", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.INFERRED))
    void updateProfile_withNoProfile_returns404() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getEmail());

        ProfileUpdateRequest request = new ProfileUpdateRequest(
                "testName",
                2021,
                List.of(new UserMajorRequest(
                        1L, MajorType.PRIMARY
                )),
                Track.PRIMARY_WITH_DOUBLE
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
    @DisplayName("POST /api/users/me/profile - returns 404 when major does not exist")
    void updateProfile_withInvalidMajorId_returns404() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getEmail());

        ProfileUpdateRequest request = new ProfileUpdateRequest(
                "testName",
                2021,
                List.of(new UserMajorRequest(
                        999L, MajorType.PRIMARY
                )),
                Track.PRIMARY_WITH_DOUBLE
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
    @DisplayName("POST /api/users/me/completed-courses - creates completed course records")
    void insertCompletedCourses_withValidRequest_insertsCompletedCourses() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getEmail());

        CompletedCourseInsertRequest request = new CompletedCourseInsertRequest(
             List.of(new CompletedCourseInsertItem(2L, Grade.A, false)));

        mockMvc.perform(post("/api/users/me/completed-courses")
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /api/users/me/completed-courses - returns 409 when completed course already exists")
    void insertCompletedCourses_withDuplicateCompletedCourse_returns409() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getEmail());
        CourseOffering offering = courseOfferingRepository.findById(1L).orElseThrow(() -> new IllegalStateException("Test setup failed: courseOffering not found"));

        userCompletedCourseRepository.save(UserCompletedCourse.builder()
                        .user(user)
                        .retake(false)
                        .courseOffering(offering)
                .build());

        CompletedCourseInsertRequest request = new CompletedCourseInsertRequest(
                List.of( new CompletedCourseInsertItem(1L, Grade.A, false)));

        mockMvc.perform(post("/api/users/me/completed-courses")
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("COMPLETED_COURSE_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("POST /api/users/me/completed-courses - returns 404 when course offering does not exist")
    @Sql(statements = "DELETE FROM user_completed_course", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.INFERRED))
    void insertCompletedCourses_withInvalidCourseOffering_returns404() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getEmail());

        CompletedCourseInsertRequest request = new CompletedCourseInsertRequest(
                List.of( new CompletedCourseInsertItem(99999L, Grade.A, false)));

        mockMvc.perform(post("/api/users/me/completed-courses")
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("COURSE_OFFERING_NOT_FOUND"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("GET /api/users/me/graduation-requirements - returns graduation requirements")
    void getGraduationRequirements_withExistingRequirements_returnsRequirements() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getEmail());

        mockMvc.perform(get("/api/users/me/graduation-requirements")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("GET /api/users/me/graduation-requirements - returns 404 when requirement status does not exist")
    @Sql(statements = "DELETE FROM user_requirement_status", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.INFERRED))
    void getGraduationRequirements_withNoRequirementStatus_returns404() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getEmail());

        mockMvc.perform(get("/api/users/me/graduation-requirements")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("USER_REQUIREMENT_STATUS_NOT_FOUND"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("PUT /api/users/me/graduation-requirements - updates requirement status")
    void updateRequirements_withValidRequest_updatesRequirementStatus() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getEmail());

        UserRequirementFulfillmentRequest request = new UserRequirementFulfillmentRequest(
                1L,true);

        mockMvc.perform(put("/api/users/me/graduation-requirements")
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(List.of(request)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        UserRequirementStatus status = userRequirementStatusRepository.findById(1L).orElseThrow(() -> new UserRequirementStatusNotFoundException(1L));
        assertThat(status.isFulfilled()).isTrue();
    }

    @Test
    @DisplayName("PUT /api/users/me/graduation-requirements - returns 404 when requirement status does not exist")
    void updateRequirements_withInvalidRequirementStatusId_returns404() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getEmail());

        UserRequirementFulfillmentRequest request = new UserRequirementFulfillmentRequest(
                9999L,true);

        mockMvc.perform(put("/api/users/me/graduation-requirements")
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(List.of(request)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("USER_REQUIREMENT_STATUS_NOT_FOUND"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("GET /api/users/me/graduation-info - returns graduation info")
    void getMainGraduationInfo_withValidUser_returnsGraduationInfo() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getEmail());

        mockMvc.perform(get("/api/users/me/graduation-info")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Tester"));
    }

    @Test
    @DisplayName("GET /api/users/me/graduation-info - returns 404 when user major does not exist")
    @Sql(statements = "DELETE FROM user_major", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.INFERRED))
    void getMainGraduationInfo_withNoUserMajor_returns404() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getEmail());

        mockMvc.perform(get("/api/users/me/graduation-info")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("USER_MAJOR_NOT_FOUND"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("GET /api/users/me/required-courses -returns required courses")
    void getMyRequiredCourses_withValidMajorType_returnsRequiredCourses() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getEmail());

        mockMvc.perform(get("/api/users/me/required-courses")
                        .header("Authorization", "Bearer " + token)
                .param("majorType", "PRIMARY"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/users/me/summary - returns user summary")
    void getSummary_withValidUser_returnsUserSummary() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getEmail());

        mockMvc.perform(get("/api/users/me/summary")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Tester"));
    }

    @Test
    @DisplayName("PATCH /api/users/me/majors - updates user major")
    void updateUserMajor_withValidRequest_updatesUserMajor() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getEmail());

        List<UserMajorUpdateRequest> requests = List.of(
                new UserMajorUpdateRequest(MajorType.PRIMARY, 1L,2L));

        mockMvc.perform(patch("/api/users/me/majors")
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(requests))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        UserMajor userMajor = userMajorRepository.findById(1L).orElseThrow(() -> new UserMajorNotFoundException(1L));
        assertThat(userMajor.getMajor().getId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("PATCH /api/users/me/majors - returns 404 when major does not exist")
    void updateUserMajor_withInvalidMajorId_returns404() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getEmail());

        List<UserMajorUpdateRequest> requests = List.of(
                new UserMajorUpdateRequest(MajorType.PRIMARY, 2L, 999L));

        mockMvc.perform(patch("/api/users/me/majors")
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(requests))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("MAJOR_NOT_FOUND"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("PATCH /api/users/me/majors/type - updates user major type")
    void updateUserMajorTypes_withValidRequest_updatesMajorType() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getEmail());

        UserMajorTypeUpdateItem updateItem = new UserMajorTypeUpdateItem(1L, MajorType.PRIMARY, MajorType.DOUBLE);
        UserMajorTypeUpdateRequest request = new UserMajorTypeUpdateRequest(List.of(updateItem), Track.PRIMARY_WITH_DOUBLE);

        mockMvc.perform(patch("/api/users/me/majors/type")
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        UserMajor userMajor = userMajorRepository.findById(1L).orElseThrow(() -> new UserMajorNotFoundException(1L));
        assertThat(userMajor.getMajorType()).isEqualTo(MajorType.DOUBLE);
    }

    @Test
    @DisplayName("PATCH /api/users/me/majors/type - returns 404 when user major does not exist")
    void updateUserMajorTypes_withInvalidUserMajorId_returns404() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getEmail());

        UserMajorTypeUpdateItem updateItem = new UserMajorTypeUpdateItem(999L, MajorType.PRIMARY, MajorType.DOUBLE);
        UserMajorTypeUpdateRequest request = new UserMajorTypeUpdateRequest(List.of(updateItem), Track.PRIMARY_WITH_DOUBLE);

        mockMvc.perform(patch("/api/users/me/majors/type")
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("USER_MAJOR_NOT_FOUND"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("PATCH /api/users/me/completed-courses - updates completed course")
    void updateCompletedCourses_withValidRequest_updatesCompletedCourse() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getEmail());

        List<CompletedCourseUpdateRequest> requests = List.of(
                new CompletedCourseUpdateRequest(1L, Grade.A_PLUS, true));

        mockMvc.perform(patch("/api/users/me/completed-courses")
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(requests))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        UserCompletedCourse userCompletedCourse = userCompletedCourseRepository.findById(1L).orElseThrow(() -> new UserCompletedCourseNotFoundException(1L));
        assertThat(userCompletedCourse.getGrade()).isEqualTo(Grade.A_PLUS);
        assertThat(userCompletedCourse.isRetake()).isTrue();
    }

    @Test
    @DisplayName("PATCH /api/users/me/completed-courses - returns 404 when completed course does not exist")
    void updateCompletedCourses_withInvalidCompletedCourseId_returns404() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getEmail());

        List<CompletedCourseUpdateRequest> requests = List.of(
                new CompletedCourseUpdateRequest(999L, Grade.A_PLUS, true));

        mockMvc.perform(patch("/api/users/me/completed-courses")
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(requests))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("USER_COMPLETED_COURSE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("POST /api/users/me/subscriptions - creates user subscriptions")
    void subscribeOrganizations_withValidRequest_insertsSubscriptions() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getEmail());

        List<UserSubscriptionRequest> requests = List.of(
                new UserSubscriptionRequest(1L, SubscriptionType.MAJOR));

        mockMvc.perform(post("/api/users/me/subscriptions")
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(requests))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("PUT /api/users/me/subscriptions - updates user subscriptions")
    void updateUserSubscriptions_withValidRequest_updatesSubscriptions() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getEmail());

        List<UserSubscriptionRequest> requests = List.of(
                new UserSubscriptionRequest(1L, SubscriptionType.MAJOR));

        mockMvc.perform(put("/api/users/me/subscriptions")
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(requests))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/users/me/ - deletes user")
    void deleteUser_withValidUser_deletesUser() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getEmail());

        mockMvc.perform(delete("/api/users/me")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
