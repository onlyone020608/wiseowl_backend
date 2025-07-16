package com.hyewon.wiseowl_backend.domain.user.dto;

import com.hyewon.wiseowl_backend.domain.user.entity.SubscriptionType;

public record UserSubscriptionRequest(Long targetId, SubscriptionType type) {
}
