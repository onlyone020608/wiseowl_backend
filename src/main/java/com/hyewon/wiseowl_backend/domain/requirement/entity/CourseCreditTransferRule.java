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
public class CourseCreditTransferRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_major_id")
    private Major toMajor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_course_id")
    private Course fromCourse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_course_id")
    private Course toCourse;

    private String note;

    private Integer appliesFromYear;

    private Integer appliesToYear;
}
