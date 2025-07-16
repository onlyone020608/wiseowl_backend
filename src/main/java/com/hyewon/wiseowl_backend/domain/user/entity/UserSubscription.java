package com.hyewon.wiseowl_backend.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    @Enumerated(EnumType.STRING)
    private SubscriptionType type;

    private Long targetId;  // Major ID or Organization ID


    private UserSubscription(User user, Long targetId, SubscriptionType type) {
        this.user = user;
        this.targetId = targetId;
        this.type = type;

    }
    public static UserSubscription of(User user,  Long targetId, SubscriptionType type) {
        return new UserSubscription(user, targetId, type);
    }




}
