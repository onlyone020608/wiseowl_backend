package com.hyewon.wiseowl_backend.integration;

import com.hyewon.wiseowl_backend.domain.user.entity.SubscriptionType;
import com.hyewon.wiseowl_backend.domain.user.entity.User;
import com.hyewon.wiseowl_backend.domain.user.entity.UserSubscription;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class NoticeControllerIT extends AbstractIntegrationTest {
    @Test
    @DisplayName("GET /api/notices/subscribed - returns user-subscribed notices grouped by source")
    void getUserSubscribedNotices_withValidSubscriptions_returnsNoticesGroupedBySource() throws Exception {
        User user = testDataLoader.getTestUser();
        String token = jwtProvider.generateAccessToken(user.getEmail());

        mockMvc.perform(get("/api/notices/subscribed")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].subscriptionName").exists())
                .andExpect(jsonPath("$[0].notices").isArray());
    }

    @Test
    @DisplayName("GET /api/notices/subscribed - should return 404 if subscribed major does not exist")
    void getUserSubscribedNotices_withInvalidMajorSubscription_returns404() throws Exception {
        User user = testDataLoader.getTestUser();
        userSubscriptionRepository.save(UserSubscription.builder()
                .user(user)
                .targetId(999L)
                .type(SubscriptionType.MAJOR)
                .build());

        String token = jwtProvider.generateAccessToken(user.getEmail());
        mockMvc.perform(get("/api/notices/subscribed")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("MAJOR_NOT_FOUND"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("GET /api/notices/subscribed - should return 404 if subscribed organization does not exist")
    void getUserSubscribedNotices_withInvalidOrganizationSubscription_returns404() throws Exception {
        User user = testDataLoader.getTestUser();
        userSubscriptionRepository.save(UserSubscription.builder()
                .user(user)
                .targetId(999L)
                .type(SubscriptionType.ORGANIZATION)
                .build());

        String token = jwtProvider.generateAccessToken(user.getEmail());

        mockMvc.perform(get("/api/notices/subscribed")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("ORGANIZATION_NOT_FOUND"))
                .andExpect(jsonPath("$.message").exists());
    }
}
