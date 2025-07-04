package com.hyewon.wiseowl_backend.domain.course.controller;

import com.hyewon.wiseowl_backend.domain.course.dto.CollegeWithMajorsDto;
import com.hyewon.wiseowl_backend.domain.course.dto.CourseCategoryListResponse;
import com.hyewon.wiseowl_backend.domain.course.dto.CourseOfferingDto;
import com.hyewon.wiseowl_backend.domain.course.dto.CourseCategoryDto;
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
    public CourseCategoryListResponse getCourseCategories(@RequestParam Long semesterId){
        List<CourseCategoryDto> result = courseService.getCourseCategoriesBySemester(semesterId);
        return new CourseCategoryListResponse(result);
    }

    @GetMapping("/offerings")
    public ResponseEntity<List<CourseOfferingDto>> getOfferings(@RequestParam Long semesterId){
        List<CourseOfferingDto> offerings = courseService.getCourseOfferingsBySemester(semesterId);
        return ResponseEntity.ok(offerings);
    }

    @GetMapping("/colleges-with-majors")
    public ResponseEntity<List<CollegeWithMajorsDto>> getCollegesWithMajors() {
        List<CollegeWithMajorsDto> result = courseService.getCollegesWithMajors();
        return ResponseEntity.ok(result);
    }
}
