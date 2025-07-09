package com.hyewon.wiseowl_backend.domain.requirement.entity;

import com.hyewon.wiseowl_backend.domain.course.entity.CourseType;
import com.hyewon.wiseowl_backend.domain.course.entity.Major;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreditRequirement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "major_id")
    private Major major;

    @Enumerated(EnumType.STRING)
    private MajorType majorType;

    @Enumerated(EnumType.STRING)
    private CourseType courseType;

    private int requiredCredits;

    private Integer appliesFromYear;

    private Integer appliesToYear;




}
