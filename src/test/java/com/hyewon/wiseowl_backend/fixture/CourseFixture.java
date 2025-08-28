package com.hyewon.wiseowl_backend.fixture;

import com.hyewon.wiseowl_backend.domain.course.entity.*;

public class CourseFixture {
    public static College aCollege() {
        return College.builder()
                .id(1L)
                .name("공과대학")
                .build();
    }

    public static Major aDefaultMajor() {
        return Major.builder()
                .id(1L)
                .build();
    }

    public static Major aMajor() {
        return Major.builder()
                .id(1L)
                .name("컴퓨터공학과")
                .build();
    }

    public static Major aMajor1(College college) {
        return Major.builder()
                .id(10L)
                .college(college)
                .name("컴퓨터공학과")
                .build();
    }

    public static Major aMajor2(College college) {
        return Major.builder()
                .id(11L)
                .name("전기전자공학과")
                .college(college)
                .build();
    }

    public static LiberalCategory aLiberalCategory() {
        return LiberalCategory.builder()
                .id(1L)
                .name("언어와문학")
                .build();
    }

    public static Course aCourse() {
        return Course.builder()
                .name("자료구조")
                .courseCodePrefix("V41006")
                .credit(3)
                .courseType(CourseType.MAJOR)
                .build();
    }

    public static Course aMajorCourse(Major major) {
        return Course.builder()
                .name("자료구조")
                .courseCodePrefix("V41006")
                .credit(3)
                .major(major)
                .courseType(CourseType.MAJOR)
                .build();
    }

    public static Course aLiberalCourse() {
        return Course.builder()
                .id(20L)
                .courseCodePrefix("GEN")
                .credit(2)
                .courseType(CourseType.GENERAL)
                .name("글쓰기")
                .build();
    }

    public static Semester aSemester() {
        return Semester.builder()
                .id(1L)
                .year(2024)
                .term(Term.FIRST)
                .build();
    }

    public static CourseOffering aMajorCourseOffering(Course course) {
        return CourseOffering.builder()
                .id(100L)
                .course(course)
                .room("0409")
                .courseCode("CSE101")
                .semester(aSemester())
                .build();
    }

    public static CourseOffering aLiberalCourseOffering(Course course) {
        return CourseOffering.builder()
                .id(200L)
                .course(course)
                .room("0409")
                .courseCode("GEN101")
                .build();
    }
}
