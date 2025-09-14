package com.hyewon.wiseowl_backend.domain.course.controller;

import com.hyewon.wiseowl_backend.domain.course.dto.CollegesWithMajorsResponse;
import com.hyewon.wiseowl_backend.domain.course.dto.CourseCategoryListResponse;
import com.hyewon.wiseowl_backend.domain.course.dto.CourseCategoryResponse;
import com.hyewon.wiseowl_backend.domain.course.dto.CourseOfferingsResponse;
import com.hyewon.wiseowl_backend.domain.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/courses")
public class CourseController {
    private final CourseService courseService;

    @GetMapping("/course-categories")
    public CourseCategoryListResponse getCourseCategories(@RequestParam Long semesterId) {
        List<CourseCategoryResponse> result = courseService.getCourseCategoriesBySemester(semesterId);
        return new CourseCategoryListResponse(result);
    }

    @GetMapping("/offerings")
    public ResponseEntity<CourseOfferingsResponse> getOfferings(@RequestParam Long semesterId) {
        CourseOfferingsResponse offerings = courseService.getCourseOfferingsBySemester(semesterId);
        return ResponseEntity.ok(offerings);
    }

    @GetMapping("/colleges-with-majors")
    public ResponseEntity<CollegesWithMajorsResponse> getCollegesWithMajors() {
        CollegesWithMajorsResponse result = courseService.getCollegesWithMajors();
        return ResponseEntity.ok(result);
    }
}
