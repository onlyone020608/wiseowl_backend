package com.hyewon.wiseowl_backend.domain.requirement.entity;

import com.hyewon.wiseowl_backend.domain.course.entity.Course;
import com.hyewon.wiseowl_backend.domain.course.entity.Major;
import com.hyewon.wiseowl_backend.domain.course.entity.Semester;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseCreditTransferRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id")
    private Semester semester;

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

    private Integer entryYearFrom;

    private Integer entryYearTo;




}
