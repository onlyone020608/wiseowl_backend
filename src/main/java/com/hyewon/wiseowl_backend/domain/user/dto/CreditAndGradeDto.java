package com.hyewon.wiseowl_backend.domain.user.dto;

import com.hyewon.wiseowl_backend.domain.user.entity.Grade;

public record CreditAndGradeDto(int credit, Grade grade) {}