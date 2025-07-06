package com.hyewon.wiseowl_backend.domain.user.entity;

import lombok.Getter;

@Getter
public enum Grade {
    A_PLUS(4.5),
    A(4.0),
    B_PLUS(3.5),
    B(3.0),
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
