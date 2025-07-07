package com.hyewon.wiseowl_backend.domain.requirement.entity;

import com.hyewon.wiseowl_backend.domain.course.entity.College;
import com.hyewon.wiseowl_backend.domain.course.entity.LiberalCategory;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CollegeRequiredLiberalCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "college_id")
    private College college;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "liberal_category_id")
    private LiberalCategory liberalCategory;

    private int requiredCredit;


}
