package com.hyewon.wiseowl_backend.domain.requirement.entity;

import com.hyewon.wiseowl_backend.domain.course.entity.College;
import com.hyewon.wiseowl_backend.domain.course.entity.LiberalCategory;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequiredLiberalCategoryByCollege {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "college_id")
    private College college;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "liberal_category_id")
    private LiberalCategory liberalCategory;

    int requiredCredit;

    private Integer appliesFromYear;

    private Integer appliesToYear;
}
