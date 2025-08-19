package com.hyewon.wiseowl_backend.domain.course.service;

import com.hyewon.wiseowl_backend.domain.course.dto.CollegeWithMajorsResponse;
import com.hyewon.wiseowl_backend.domain.course.dto.CourseCategoryResponse;
import com.hyewon.wiseowl_backend.domain.course.dto.CourseOfferingResponse;
import com.hyewon.wiseowl_backend.domain.course.dto.MajorDto;
import com.hyewon.wiseowl_backend.domain.course.entity.College;
import com.hyewon.wiseowl_backend.domain.course.entity.LiberalCategory;
import com.hyewon.wiseowl_backend.domain.course.entity.Major;
import com.hyewon.wiseowl_backend.domain.course.repository.CourseOfferingRepository;
import com.hyewon.wiseowl_backend.domain.course.repository.MajorRepository;
import com.hyewon.wiseowl_backend.global.exception.CourseNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {
    private final CourseOfferingRepository courseOfferingRepository;
    private final MajorRepository majorRepository;

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

    @Transactional(readOnly = true)
    public List<CourseOfferingResponse> getCourseOfferingsBySemester(Long semesterId) {
        return courseOfferingRepository.findCourseOfferingsBySemester(semesterId);
    }

    public List<CollegeWithMajorsResponse> getCollegesWithMajors() {
        List<Major> majors = majorRepository.findAllWithCollege();

        Map<College, List<Major>> grouped = majors.stream()
                .collect(Collectors.groupingBy(Major::getCollege));

        return grouped.entrySet().stream()
                .map(entry -> {
                    College college = entry.getKey();
                    List<MajorDto> majorDtos = entry.getValue().stream()
                            .map(m -> new MajorDto(m.getId(), m.getName()))
                            .toList();

                    return new CollegeWithMajorsResponse(
                            college.getId(),
                            college.getName(),
                            majorDtos
                    );
                })
                .toList();
    }
}
