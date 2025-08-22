package com.hyewon.wiseowl_backend.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public enum Grade {
    @JsonProperty("A+")
    A_PLUS(4.5),
    A(4.0),
    @JsonProperty("B+")
    B_PLUS(3.5),
    B(3.0),
    @JsonProperty("C+")
    C_PLUS(2.5),
    C(2.0),
    D(1.0),
    F(0.0);

    private final double gradePoint;

    Grade(double gradePoint) {
        this.gradePoint = gradePoint;
    }

    public double getGradePoint() {
        return gradePoint;
    }
}
