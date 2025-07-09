package com.hyewon.wiseowl_backend.domain.user.entity;

import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorRequirement;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRequirementStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "major_requirement_id")
    private MajorRequirement majorRequirement;

    private boolean fulfilled;

    private UserRequirementStatus(User user, MajorRequirement majorRequirement) {

        this.user = user;
        this.majorRequirement = majorRequirement;
    }

    public static UserRequirementStatus of(User user, MajorRequirement majorRequirement) {
        return new UserRequirementStatus(user, majorRequirement);
    }

    public void updateFulfilled(boolean fulfilled){
        this.fulfilled = fulfilled;
    }
}
