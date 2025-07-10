package com.hyewon.wiseowl_backend.domain.requirement.entity;

import com.hyewon.wiseowl_backend.domain.course.entity.Course;
import com.hyewon.wiseowl_backend.domain.course.entity.Major;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequiredMajorCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "major_id")
    private Major major;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @Enumerated(EnumType.STRING)
    private MajorType majorType;


    private Integer appliesFromYear;

    private Integer appliesToYear;


}
