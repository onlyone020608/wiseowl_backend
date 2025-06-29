package com.hyewon.wiseowl_backend.domain.requirement;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LanguageTestRequirement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer minScore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "major_requirement_id")
    private MajorRequirement majorRequirement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_test_id")
    private LanguageTest languageTest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "required_level_id")
    private LanguageTestLevel languageTestLevel;
}
