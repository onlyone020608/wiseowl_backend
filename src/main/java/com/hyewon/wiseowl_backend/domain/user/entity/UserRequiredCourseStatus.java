package com.hyewon.wiseowl_backend.domain.user.entity;

import com.hyewon.wiseowl_backend.domain.course.entity.CourseType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRequiredCourseStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private CourseType courseType;

    private Long requiredCourseId;

    private boolean fulfilled;

    private UserRequiredCourseStatus(User user, CourseType courseType,  Long requiredCourseId) {
        this.user = user;
        this.courseType = courseType;
        this.requiredCourseId = requiredCourseId;
    }

    public static UserRequiredCourseStatus of(User user, CourseType courseType,  Long requiredCourseId) {
        return new UserRequiredCourseStatus(user, courseType, requiredCourseId);
    }

}
