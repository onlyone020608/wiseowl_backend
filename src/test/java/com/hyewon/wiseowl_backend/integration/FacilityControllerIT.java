package com.hyewon.wiseowl_backend.integration;

import com.hyewon.wiseowl_backend.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FacilityControllerIT extends AbstractIntegrationTest {
    @Test
    @DisplayName("GET /api/facilities - returns facilities grouped by building")
    void getFacilities_success() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getEmail());

        mockMvc.perform(get("/api/facilities")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /api/facilities - should return 404 when no facility exists")
    @Sql(statements = "DELETE FROM facility", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getFacilities_facilityNotFound() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getEmail());

        mockMvc.perform(get("/api/facilities")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("FACILITY_NOT_FOUND"))
                .andExpect(jsonPath("$.message").exists());
    }
}
