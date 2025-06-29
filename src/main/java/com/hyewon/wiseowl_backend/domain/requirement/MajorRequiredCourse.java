package com.hyewon.wiseowl_backend.domain.requirement;

import com.hyewon.wiseowl_backend.domain.course.Course;
import com.hyewon.wiseowl_backend.domain.course.Major;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MajorRequiredCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "major_id")
    private Major major;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    private Integer appliesFromYear;

    private Integer appliesToYear;
}
