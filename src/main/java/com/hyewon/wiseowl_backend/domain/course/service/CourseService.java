package com.hyewon.wiseowl_backend.domain.course.service;

import com.hyewon.wiseowl_backend.domain.course.dto.*;
import com.hyewon.wiseowl_backend.domain.course.entity.College;
import com.hyewon.wiseowl_backend.domain.course.entity.LiberalCategory;
import com.hyewon.wiseowl_backend.domain.course.entity.Major;
import com.hyewon.wiseowl_backend.domain.course.repository.CourseOfferingRepository;
import com.hyewon.wiseowl_backend.domain.course.repository.MajorRepository;
import com.hyewon.wiseowl_backend.global.exception.CourseNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {
    private final CourseOfferingRepository courseOfferingRepository;
    private final MajorRepository majorRepository;

    @Cacheable(value = "courseCategories", key = "#semesterId")
    @Transactional(readOnly = true)
    public List<CourseCategoryResponse> getCourseCategoriesBySemester(Long semesterId) {
        List<Major> majors = courseOfferingRepository.findDistinctMajorsBySemesterId(semesterId);
        List<LiberalCategory> liberals = courseOfferingRepository.findDistinctLiberalCategoriesBySemester(semesterId);

        if (majors.isEmpty() && liberals.isEmpty()) {
            throw new CourseNotFoundException("No course categories found for semesterId: " + semesterId);
        }

        List<CourseCategoryResponse> result = new ArrayList<>();
        result.addAll(majors.stream().map(CourseCategoryResponse::fromMajor).toList());
        result.addAll(liberals.stream().map(CourseCategoryResponse::fromLiberal).toList());
        return result;
    }

    @Cacheable(value = "courseOfferings", key = "#semesterId")
    @Transactional(readOnly = true)
    public CourseOfferingsResponse getCourseOfferingsBySemester(Long semesterId) {
        List<CourseOfferingResponse> offerings =
                courseOfferingRepository.findCourseOfferingsBySemester(semesterId);

        return CourseOfferingsResponse.from(offerings);
    }

    @Cacheable(value = "collegesWithMajors")
    @Transactional(readOnly = true)
    public CollegesWithMajorsResponse getCollegesWithMajors() {
        List<Major> majors = majorRepository.findAllWithCollege();

        Map<College, List<Major>> grouped = majors.stream()
                .collect(Collectors.groupingBy(Major::getCollege));

        List<CollegeWithMajorsResponse> result = grouped.entrySet().stream()
                .sorted(Comparator.comparing(entry -> entry.getKey().getName()))
                .map(entry -> {
                    College college = entry.getKey();

                    List<MajorDto> majorDtos = entry.getValue().stream()
                            .sorted(Comparator.comparing(Major::getName))
                            .map(m -> new MajorDto(m.getId(), m.getName()))
                            .toList();

                    return new CollegeWithMajorsResponse(
                            college.getId(),
                            college.getName(),
                            majorDtos
                    );
                })
                .toList();

        return CollegesWithMajorsResponse.from(result);
    }
}
