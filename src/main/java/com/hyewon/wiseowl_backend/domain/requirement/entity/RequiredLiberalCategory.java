package com.hyewon.wiseowl_backend.domain.requirement.entity;

import com.hyewon.wiseowl_backend.domain.course.entity.LiberalCategory;
import com.hyewon.wiseowl_backend.domain.course.entity.Major;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequiredLiberalCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "major_id")
    private Major major;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "liberal_category_id")
    private LiberalCategory liberalCategory;

    int requiredCredit;

    private Integer appliesFromYear;

    private Integer appliesToYear;
}
