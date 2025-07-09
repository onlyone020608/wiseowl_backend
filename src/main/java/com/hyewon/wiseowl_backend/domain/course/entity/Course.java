package com.hyewon.wiseowl_backend.domain.course.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "major_id")
    private Major major;

    @NotBlank
    private String name;

    @NotBlank
    private String courseCodePrefix;

    private int credit;

    @Enumerated(EnumType.STRING)
    private CourseType courseType;






}
