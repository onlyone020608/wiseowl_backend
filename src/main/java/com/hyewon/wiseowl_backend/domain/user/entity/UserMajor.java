package com.hyewon.wiseowl_backend.domain.user.entity;

import com.hyewon.wiseowl_backend.domain.course.entity.Major;
import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserMajor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "major_id")
    private Major major;

    @Enumerated(EnumType.STRING)
    private MajorType majorType;

    private UserMajor(User user, Major major, MajorType majorType) {
        this.user = user;
        this.major = major;
        this.majorType = majorType;
    }

    public static UserMajor of(User user, Major major, MajorType majorType) {
        return new UserMajor(user, major, majorType);
    }




}
