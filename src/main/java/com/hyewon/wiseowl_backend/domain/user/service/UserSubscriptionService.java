package com.hyewon.wiseowl_backend.domain.user.service;

import com.hyewon.wiseowl_backend.domain.user.entity.UserSubscription;
import com.hyewon.wiseowl_backend.domain.user.repository.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserSubscriptionService {

    private final UserSubscriptionRepository userSubscriptionRepository;

    @Transactional(readOnly = true)
    public List<UserSubscription> getSubscriptions(Long userId) {
        return userSubscriptionRepository.findAllByUserId(userId);
    }
}
