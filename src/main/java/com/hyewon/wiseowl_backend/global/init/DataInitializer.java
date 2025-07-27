package com.hyewon.wiseowl_backend.global.init;

import com.hyewon.wiseowl_backend.domain.course.entity.*;
import com.hyewon.wiseowl_backend.domain.course.repository.*;
import com.hyewon.wiseowl_backend.global.exception.CourseNotFoundException;
import com.hyewon.wiseowl_backend.global.exception.MajorNotFoundException;
import com.hyewon.wiseowl_backend.global.exception.SemesterNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final BuildingRepository buildingRepository;
    private final SemesterRepository semesterRepository;
    private final LiberalCategoryRepository liberalCategoryRepository;
    private final CourseRepository courseRepository;
    private final MajorRepository majorRepository;
    private final CourseOfferingRepository courseOfferingRepository;

    @Override
    public void run(String... args) throws Exception {
        if (buildingRepository.count() == 0) {
            loadBuildings();
        }
        if(semesterRepository.count() == 0) {
            loadSemesters();
        }
        if(liberalCategoryRepository.count() ==0){
            loadLiberalCategory();
        }
        if(courseRepository.count() == 0) {
            loadCourse();
        }
        if(courseOfferingRepository.count() == 0) {
            loadCourseOffering();
        }

    }

    private void loadBuildings() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/building.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        boolean isFirst = true;

        while ((line = reader.readLine()) != null) {
            if (isFirst) { isFirst = false; continue; }

            String[] tokens = line.split(",");
            Integer buildingNumber = Integer.parseInt(tokens[0].trim());
            String name = tokens[1].trim();

            Building building = Building.builder()
                    .buildingNumber(buildingNumber)
                    .name(name)
                    .build();

            buildingRepository.save(building);
        }


    }

    private void loadSemesters() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/semester.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        boolean isFirst = true;

        while ((line = reader.readLine()) != null) {
            if (isFirst) { isFirst = false; continue; }

            String[] tokens = line.split(",");
            int year = Integer.parseInt(tokens[0].trim());
            Term term = Term.valueOf(tokens[1].trim().toUpperCase());

            Semester semester = Semester.builder()
                    .year(year)
                    .term(term)
                    .build();
            semesterRepository.save(semester);
        }


    }

    private void loadLiberalCategory() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/liberal_category.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        boolean isFirst = true;

        while ((line = reader.readLine()) != null) {
            if (isFirst) { isFirst = false; continue; }

            String[] tokens = line.split(",");
            String name = tokens[0].trim();

            LiberalCategory liberalCategory = LiberalCategory.builder()
                    .name(name)
                    .build();

            liberalCategoryRepository.save(liberalCategory);
        }
    }

    private void loadCourse() throws IOException {

        List<String> files = List.of(
                "data/course_2024_1_liberal.csv"
        );
        for (String path : files) {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path);
            if (inputStream == null) continue;

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            boolean isFirst = true;

            while ((line = reader.readLine()) != null) {
                if (isFirst) {
                    isFirst = false;
                    continue;
                }

                String[] tokens = line.split(",");
                Long majorId = Long.parseLong(tokens[0].trim());
                String name  = tokens[1].trim();
                String courseCode = tokens[2].trim();
                int credit = Integer.parseInt(tokens[3].trim());
                CourseType courseType = CourseType.valueOf(tokens[4].trim().toUpperCase());

                Major major = majorRepository.findById(majorId).orElseThrow(() ->
                        new MajorNotFoundException(majorId));


                Course course = Course.builder()
                        .major(major)
                        .name(name)
                        .courseCodePrefix(courseCode)
                        .credit(credit)
                        .courseType(courseType)
                                .build();

                courseRepository.save(course);
            }
        }


    }

    private void loadCourseOffering() throws IOException {

        List<String> files = List.of(
                "data/course_offering_2024_1_liberal.csv"
        );
        for (String path : files) {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path);
            if (inputStream == null) continue;

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            boolean isFirst = true;

            while ((line = reader.readLine()) != null) {
                if (isFirst) {
                    isFirst = false;
                    continue;
                }

                String[] tokens = line.split(",");
                Long courseId = Long.parseLong(tokens[0].trim());
                Long semesterId = Long.parseLong(tokens[1].trim());
                String professor  = tokens[2].trim();
                String classtime = tokens[3].trim();
                String courseCode= tokens[4].trim();
                String room = tokens[5].trim();

                Course course = courseRepository.findById(courseId).orElseThrow(() ->
                        new CourseNotFoundException(courseId));

                Semester semester = semesterRepository.findById(semesterId).orElseThrow(() ->
                        new SemesterNotFoundException(semesterId));



                CourseOffering courseOffering = CourseOffering.builder()
                        .course(course)
                        .semester(semester)
                        .professor(professor)
                        .classTime(classtime)
                        .courseCode(courseCode)
                        .room(room)
                        .build();

                courseOfferingRepository.save(courseOffering);
            }


        }


    }


}
