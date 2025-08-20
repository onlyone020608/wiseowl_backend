package com.hyewon.wiseowl_backend.global.init;

import com.hyewon.wiseowl_backend.domain.course.entity.*;
import com.hyewon.wiseowl_backend.domain.course.repository.*;
import com.hyewon.wiseowl_backend.domain.facility.entity.Facility;
import com.hyewon.wiseowl_backend.domain.facility.entity.FacilityCategory;
import com.hyewon.wiseowl_backend.domain.facility.repository.FacilityRepository;
import com.hyewon.wiseowl_backend.domain.notice.entity.Organization;
import com.hyewon.wiseowl_backend.domain.notice.repository.OrganizationRepository;
import com.hyewon.wiseowl_backend.domain.requirement.entity.*;
import com.hyewon.wiseowl_backend.domain.requirement.repository.*;
import com.hyewon.wiseowl_backend.global.exception.*;
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
    private final LiberalCategoryCourseRepository liberalCategoryCourseRepository;
    private final CreditRequirementRepository creditRequirementRepository;
    private final RequirementRepository requirementRepository;
    private final LanguageTestRepository languageTestRepository;
    private final LanguageTestLevelRepository languageTestLevelRepository;
    private final MajorRequirementRepository majorRequirementRepository;
    private final LanguageTestRequirementRepository languageTestRequirementRepository;
    private final RequiredLiberalCategoryRepository requiredLiberalCategoryRepository;
    private final RequiredMajorCourseRepository requiredMajorCourseRepository;
    private final CollegeRepository collegeRepository;
    private final FacilityRepository facilityRepository;
    private final OrganizationRepository organizationRepository;
    private final CourseCreditTransferRuleRepository courseCreditTransferRuleRepository;

    @Override
    public void run(String... args) throws Exception {
        if (collegeRepository.count() == 0) {
            loadColleges();
        }
        if (majorRepository.count() == 0) {
            loadMajors();
        }
        if (buildingRepository.count() == 0) {
            loadBuildings();
        }
        if (facilityRepository.count() == 0) {
            loadFacilities();
        }
        if (organizationRepository.count() == 0) {
            loadOrganizations();
        }
        if (semesterRepository.count() == 0) {
            loadSemesters();
        }
        if (liberalCategoryRepository.count() ==0){
            loadLiberalCategory();
        }
        if (courseRepository.count() == 0) {
            loadCourse();
        }
        if (courseOfferingRepository.count() == 0) {
            loadCourseOffering();
        }
        if (liberalCategoryCourseRepository.count() == 0 ) {
            loadLiberalCategoryCourse();
        }
        if (creditRequirementRepository.count() == 0) {
            loadCreditRequirement();
        }
        if (requirementRepository.count() == 0) {
            loadRequirements();
        }
        if (languageTestRepository.count() == 0) {
            loadLanguageTests();
        }
        if (languageTestLevelRepository.count() == 0) {
            loadLanguageTestLevels();
        }
        if (majorRequirementRepository.count() == 0) {
            loadMajorRequirements();
        }
        if (languageTestRequirementRepository.count() == 0) {
            loadLanguageTestRequirements();
        }
        if (requiredLiberalCategoryRepository.count() == 0) {
            loadRequiredLiberalCategories();
        }
        if (requiredMajorCourseRepository.count() == 0) {
            loadRequiredMajorCourses();
        }
        if (courseCreditTransferRuleRepository.count() == 0) {
            loadCourseCreditTransferRules();
        }
    }

    private void loadColleges() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/college.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        boolean isFirst = true;

        while ((line = reader.readLine()) != null) {
            if (isFirst) { isFirst = false; continue; }

            String[] tokens = line.split(",");
            String name = tokens[0].trim();

            College college = College.builder()
                    .name(name)
                    .build();

            collegeRepository.save(college);
        }
    }

    private void loadMajors() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/major.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        boolean isFirst = true;

        while ((line = reader.readLine()) != null) {
            if (isFirst) { isFirst = false; continue; }

            String[] tokens = line.split(",");
            Long collegeId = Long.parseLong(tokens[0].trim());
            String name = tokens[1].trim();
            boolean onlyDbMajor = Boolean.parseBoolean(tokens[2].trim());

            College college = collegeRepository.findById(collegeId).orElseThrow(() -> new CollegeNotFoundException(collegeId));

            Major major = Major.builder()
                    .college(college)
                    .name(name)
                    .onlyDbMajor(onlyDbMajor)
                    .build();

            majorRepository.save(major);
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

    private void loadFacilities() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/facility.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        boolean isFirst = true;

        while ((line = reader.readLine()) != null) {
            if (isFirst) { isFirst = false; continue; }

            String[] tokens = line.split(",");
            Long buildingId = Long.parseLong(tokens[0].trim());
            String name = tokens[1].trim();
            FacilityCategory facilityCategory= FacilityCategory.valueOf(tokens[2].trim().toUpperCase());
            Integer floor = Integer.parseInt(tokens[3].trim());
            String description = tokens[4].trim();

            Building building = buildingRepository.findById(buildingId).orElseThrow(() -> new BuildingNotFoundException(buildingId));

            Facility facility = Facility.builder()
                    .building(building)
                    .name(name)
                    .facilityCategory(facilityCategory)
                    .floor(floor)
                    .description(description)
                    .build();

            facilityRepository.save(facility);
        }
    }

    private void loadOrganizations() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/organization.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        boolean isFirst = true;

        while ((line = reader.readLine()) != null) {
            if (isFirst) { isFirst = false; continue; }

            String[] tokens = line.split(",");
            String name = tokens[0].trim();
            String homepageUrl = tokens[1].trim();

            Organization organization = Organization.builder()
                    .name(name)
                    .homepageUrl(homepageUrl)
                    .build();

            organizationRepository.save(organization);
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
            if (isFirst) {
                isFirst = false;
                continue;
            }

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
                "data/course_2024_1_liberal.csv",
                "data/course_2024_1_major.csv"
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
                "data/course_offering_2024_1_liberal.csv",
                "data/course_offering_2024_1_major.csv"
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

    private void loadLiberalCategoryCourse() throws IOException {
        List<String> files = List.of(
                "data/liberal_category_course_2024_1.csv"
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
                Long liberalCategoryId = Long.parseLong(tokens[1].trim());

                Course course = courseRepository.findById(courseId).orElseThrow(() ->
                        new CourseNotFoundException(courseId));

                LiberalCategory liberalCategory = liberalCategoryRepository.findById(liberalCategoryId)
                        .orElseThrow(() -> new LiberalCategoryNotFoundException(liberalCategoryId));

               LiberalCategoryCourse liberalCategoryCourse = LiberalCategoryCourse.builder()
                       .course(course)
                       .liberalCategory(liberalCategory)
                       .build();

               liberalCategoryCourseRepository.save(liberalCategoryCourse);
            }
        }
    }

    private void loadCreditRequirement() throws IOException {
        List<String> files = List.of(
                "data/credit_requirement_2024.csv"
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
                MajorType majorType = MajorType.valueOf(tokens[1].trim().toUpperCase());
                CourseType courseType = CourseType.valueOf(tokens[2].trim().toUpperCase());
                int requiredCredit = Integer.parseInt(tokens[3].trim());
                Track track = Track.valueOf(tokens[4].trim().toUpperCase());
                Integer appliesFromYear = Integer.parseInt(tokens[5].trim());
                Integer appliesToYear = Integer.parseInt(tokens[6].trim());

                Major major = majorRepository.findById(majorId).orElseThrow(() ->new MajorNotFoundException(majorId));

                CreditRequirement creditRequirement = CreditRequirement.builder()
                        .major(major)
                        .majorType(majorType)
                        .courseType(courseType)
                        .requiredCredits(requiredCredit)
                        .track(track)
                        .appliesFromYear(appliesFromYear)
                        .appliesToYear(appliesToYear)
                        .build();

                creditRequirementRepository.save(creditRequirement);
            }
        }
    }

    private void loadRequirements() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/requirement.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        boolean isFirst = true;

        while ((line = reader.readLine()) != null) {
            if (isFirst) { isFirst = false; continue; }

            String[] tokens = line.split(",");
            String name = tokens[0].trim();

            Requirement requirement = Requirement.builder()
                    .name(name)
                    .build();

            requirementRepository.save(requirement);
        }
    }

    private void loadLanguageTests() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/language_test.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        boolean isFirst = true;

        while ((line = reader.readLine()) != null) {
            if (isFirst) { isFirst = false; continue; }

            String[] tokens = line.split(",");
            String name = tokens[0].trim();

            LanguageTest languageTest = LanguageTest.builder()
                    .name(name)
                    .build();

            languageTestRepository.save(languageTest);
        }
    }

    private void loadLanguageTestLevels() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/language_test_level.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        boolean isFirst = true;

        while ((line = reader.readLine()) != null) {
            if (isFirst) { isFirst = false; continue; }

            String[] tokens = line.split(",");
            Long languageTestId = Long.parseLong(tokens[0].trim());
            String levelCode = tokens[1].trim();
            int levelOrder = Integer.parseInt(tokens[2].trim());

            LanguageTest languageTest = languageTestRepository.findById(languageTestId).orElseThrow(() -> new LanguageTestNotFoundException(languageTestId));

            LanguageTestLevel languageTestLevel = LanguageTestLevel.builder()
                    .languageTest(languageTest)
                    .levelOrder(levelOrder)
                    .levelCode(levelCode)
                    .build();

            languageTestLevelRepository.save(languageTestLevel);
        }
    }

    private void loadMajorRequirements() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/major_requirement.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        boolean isFirst = true;

        while ((line = reader.readLine()) != null) {
            if (isFirst) { isFirst = false; continue; }

            String[] tokens = line.split(",");
            Long requirementId = Long.parseLong(tokens[0].trim());
            Long majorId = Long.parseLong(tokens[1].trim());
            MajorType majorType = MajorType.valueOf(tokens[2].trim().toUpperCase());
            String description = tokens[3].trim();
            Integer appliesFromYear = Integer.parseInt(tokens[4].trim());
            Integer appliesToYear = Integer.parseInt(tokens[5].trim());

            Requirement requirement = requirementRepository.findById(requirementId).orElseThrow(() -> new RequirementNotFoundException(requirementId));
            Major major = majorRepository.findById(majorId).orElseThrow(() -> new MajorNotFoundException(majorId));

            MajorRequirement majorRequirement = MajorRequirement.builder()
                    .requirement(requirement)
                    .major(major)
                    .majorType(majorType)
                    .description(description)
                    .appliesFromYear(appliesFromYear)
                    .appliesToYear(appliesToYear)
                    .build();

            majorRequirementRepository.save(majorRequirement);
        }
    }

    private void loadLanguageTestRequirements() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/language_test_requirement.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        boolean isFirst = true;

        while ((line = reader.readLine()) != null) {
            if (isFirst) { isFirst = false; continue; }

            String[] tokens = line.split(",");
            Integer minScore = Integer.parseInt(tokens[0].trim());
            Long majorRequirementId = Long.parseLong(tokens[1].trim());
            Long languageTestId = Long.parseLong(tokens[2].trim());
            Long languageTestLevelId = Long.parseLong(tokens[3].trim());

            MajorRequirement majorRequirement = majorRequirementRepository.findById(majorRequirementId).orElseThrow(() -> new MajorRequirementNotFoundException(majorRequirementId));
            LanguageTest languageTest = languageTestRepository.findById(languageTestId).orElseThrow(() -> new LanguageTestNotFoundException(languageTestId));
            LanguageTestLevel languageTestLevel = languageTestLevelRepository.findById(languageTestLevelId).orElseThrow(() -> new LanguageTestLevelNotFoundException(languageTestLevelId));

            LanguageTestRequirement languageTestRequirement = LanguageTestRequirement.builder()
                    .minScore(minScore)
                    .majorRequirement(majorRequirement)
                    .languageTest(languageTest)
                    .languageTestLevel(languageTestLevel)
                    .build();

            languageTestRequirementRepository.save(languageTestRequirement);
        }
    }

    private void loadRequiredLiberalCategories() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/required_liberal_category.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        boolean isFirst = true;

        while ((line = reader.readLine()) != null) {
            if (isFirst) { isFirst = false; continue; }

            String[] tokens = line.split(",");
            Long majorId = Long.parseLong(tokens[0].trim());
            Long liberalCategoryId = Long.parseLong(tokens[1].trim());
            int requiredCredit = Integer.parseInt(tokens[2].trim());
            Integer appliesFromYear = Integer.parseInt(tokens[3].trim());
            Integer appliesToYear = Integer.parseInt(tokens[4].trim());

            Major major = majorRepository.findById(majorId).orElseThrow(() -> new MajorNotFoundException(majorId));
            LiberalCategory liberalCategory = liberalCategoryRepository.findById(liberalCategoryId).orElseThrow(() -> new LiberalCategoryNotFoundException(liberalCategoryId));

            RequiredLiberalCategory requiredLiberalCategory = RequiredLiberalCategory.builder()
                    .major(major)
                    .liberalCategory(liberalCategory)
                    .requiredCredit(requiredCredit)
                    .appliesFromYear(appliesFromYear)
                    .appliesToYear(appliesToYear)
                    .build();

            requiredLiberalCategoryRepository.save(requiredLiberalCategory);
        }
    }

    private void loadRequiredMajorCourses() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/required_major_course.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        boolean isFirst = true;

        while ((line = reader.readLine()) != null) {
            if (isFirst) { isFirst = false; continue; }

            String[] tokens = line.split(",");
            Long majorId = Long.parseLong(tokens[0].trim());
            Long courseId = Long.parseLong(tokens[1].trim());
            MajorType majorType = MajorType.valueOf(tokens[2].trim().toUpperCase());
            Integer appliesFromYear = Integer.parseInt(tokens[3].trim());
            Integer appliesToYear = Integer.parseInt(tokens[4].trim());

            Major major = majorRepository.findById(majorId).orElseThrow(() -> new MajorNotFoundException(majorId));
            Course course = courseRepository.findById(courseId).orElseThrow(() -> new CourseNotFoundException(courseId));

            RequiredMajorCourse requiredMajorCourse = RequiredMajorCourse.builder()
                    .major(major)
                    .course(course)
                    .majorType(majorType)
                    .appliesFromYear(appliesFromYear)
                    .appliesToYear(appliesToYear)
                    .build();

            requiredMajorCourseRepository.save(requiredMajorCourse);
        }
    }

    private void loadCourseCreditTransferRules() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/course_credit_transfer_rule.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        boolean isFirst = true;

        while ((line = reader.readLine()) != null) {
            if (isFirst) { isFirst = false; continue; }

            String[] tokens = line.split(",");
            Long toMajorId = parseNullableLong(tokens[0]);
            Long fromCourseId = parseNullableLong(tokens[1]);
            Long toCourseId = parseNullableLong(tokens[2]);
            String note =  tokens[3].trim();
            Integer appliesFromYear = parseNullableInt(tokens[4]);
            Integer appliesToYear = parseNullableInt(tokens[5]);

            Major major = (toMajorId != null && !toMajorId.toString().isBlank())
                    ? majorRepository.findById(toMajorId).orElseThrow(() -> new MajorNotFoundException(toMajorId))
                    : null;

            Course fromCourse = (fromCourseId != null && !fromCourseId.toString().isBlank())
                    ? courseRepository.findById(fromCourseId).orElseThrow(() -> new CourseNotFoundException(fromCourseId))
                    : null;

            Course toCourse = (toCourseId != null && !toCourseId.toString().isBlank())
                    ? courseRepository.findById(toCourseId).orElseThrow(() -> new CourseNotFoundException(toCourseId))
                    : null;

            CourseCreditTransferRule courseCreditTransferRule = CourseCreditTransferRule.builder()
                    .toMajor(major)
                    .toCourse(toCourse)
                    .fromCourse(fromCourse)
                    .note(note)
                    .appliesFromYear(appliesFromYear)
                    .appliesToYear(appliesToYear)
                    .build();

            courseCreditTransferRuleRepository.save(courseCreditTransferRule);
        }
    }

    private Long parseNullableLong(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        return Long.parseLong(token.trim());
    }

    private Integer parseNullableInt(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        return Integer.parseInt(token.trim());
    }
}
