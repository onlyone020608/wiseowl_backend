package com.hyewon.wiseowl_backend.domain;

import com.hyewon.wiseowl_backend.domain.user.entity.SubscriptionType;
import com.hyewon.wiseowl_backend.domain.user.entity.User;
import com.hyewon.wiseowl_backend.domain.user.entity.UserSubscription;
import com.hyewon.wiseowl_backend.domain.user.repository.UserSubscriptionRepository;
import com.hyewon.wiseowl_backend.domain.user.service.UserSubscriptionService;
import com.hyewon.wiseowl_backend.fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UserSubscriptionServiceTest {
    @InjectMocks UserSubscriptionService userSubscriptionService;
    @Mock UserSubscriptionRepository userSubscriptionRepository;

    private UserSubscription userSubscription;
    private User user;

    @BeforeEach
    void setUp() {
        user = UserFixture.aDefaultUser();
        userSubscription = UserSubscription.builder()
                .user(user)
                .targetId(2L)
                .type(SubscriptionType.MAJOR)
                .build();
    }

    @Test
    @DisplayName("returns user's subscription list when user has subscriptions")
    void shouldReturnUserSubscriptions_whenUserHasSubscriptions() {
        // given
        Long userId = 1L;
        given(userSubscriptionRepository.findAllByUserId(userId)).willReturn(
                List.of(userSubscription));

        // when
        List<UserSubscription> subscriptions = userSubscriptionService.getSubscriptions(userId);

        // then
        assertThat(subscriptions).hasSize(1);
        assertThat(userSubscription).isEqualTo(subscriptions.get(0));
    }
}

