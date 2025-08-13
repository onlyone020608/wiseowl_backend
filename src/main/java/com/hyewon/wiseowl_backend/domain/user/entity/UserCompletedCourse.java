package com.hyewon.wiseowl_backend.domain.user.entity;

import com.hyewon.wiseowl_backend.domain.course.entity.CourseOffering;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCompletedCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_offering_id")
    private CourseOffering courseOffering;

    @Enumerated(EnumType.STRING)
    private Grade grade;

    private boolean retake;

    private UserCompletedCourse(User user, CourseOffering courseOffering, Grade grade, boolean retake) {
        this.user = user;
        this.courseOffering = courseOffering;
        this.grade = grade;
        this.retake = retake;
    }

    public void updateGrade(Grade grade) {
        this.grade = grade;
    }

    public void updateRetake(boolean retake) {
        this.retake = retake;
    }

    public static UserCompletedCourse of(User user, CourseOffering courseOffering, Grade grade, boolean retake) {
        return new UserCompletedCourse(user, courseOffering, grade, retake);
    }
}
