package com.hyewon.wiseowl_backend.domain.user.event;

import com.hyewon.wiseowl_backend.domain.course.entity.Course;
import com.hyewon.wiseowl_backend.domain.course.entity.CourseType;
import com.hyewon.wiseowl_backend.domain.course.entity.LiberalCategory;
import com.hyewon.wiseowl_backend.domain.course.repository.LiberalCategoryCourseRepository;
import com.hyewon.wiseowl_backend.domain.requirement.entity.RequiredLiberalCategoryByCollege;
import com.hyewon.wiseowl_backend.domain.requirement.entity.RequiredMajorCourse;
import com.hyewon.wiseowl_backend.domain.requirement.repository.CourseCreditTransferRuleRepository;
import com.hyewon.wiseowl_backend.domain.requirement.repository.RequiredLiberalCategoryByCollegeRepository;
import com.hyewon.wiseowl_backend.domain.requirement.repository.RequiredMajorCourseRepository;
import com.hyewon.wiseowl_backend.domain.user.entity.UserCompletedCourse;
import com.hyewon.wiseowl_backend.domain.user.entity.UserRequiredCourseStatus;
import com.hyewon.wiseowl_backend.domain.user.repository.UserCompletedCourseRepository;
import com.hyewon.wiseowl_backend.domain.user.repository.UserRequiredCourseStatusRepository;
import com.hyewon.wiseowl_backend.global.exception.RequiredLiberalCategoryNotFoundException;
import com.hyewon.wiseowl_backend.global.exception.RequiredMajorCourseNotFoundException;
import com.hyewon.wiseowl_backend.global.exception.UserCompletedCourseNotFoundException;
import com.hyewon.wiseowl_backend.global.exception.UserRequiredCourseStatusNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class CompletedCoursesEventListener {
    private final UserRequiredCourseStatusRepository statusRepository;
    private final CourseCreditTransferRuleRepository ruleRepository;
    private final RequiredMajorCourseRepository requiredMajorCourseRepository;
    private final RequiredLiberalCategoryByCollegeRepository requiredLiberalCategoryByCollegeRepository;
    private final UserCompletedCourseRepository userCompletedCourseRepository;
    private final LiberalCategoryCourseRepository liberalCategoryCourseRepository;

    @TransactionalEventListener
    public void handle(CompletedCoursesRegisteredEvent event) {
        List<UserRequiredCourseStatus> userStatuses = statusRepository.findAllByUserId(event.getUserId());
        if(userStatuses.isEmpty()){
            throw new UserRequiredCourseStatusNotFoundException(event.getUserId());
        }
        List<UserCompletedCourse> completedCourses = event.getCompletedCourses();
        Map<CourseType, List<UserCompletedCourse>> groupedByType = completedCourses.stream().collect(Collectors.groupingBy(cc -> cc.getCourseOffering().getCourse().getCourseType()));
        List<UserCompletedCourse> majorCompletedCourses = groupedByType.getOrDefault(CourseType.MAJOR, List.of());
        for(UserRequiredCourseStatus status : userStatuses) {
            if(status.isFulfilled() || status.getCourseType() != CourseType.MAJOR) continue;

            boolean fulfilled = majorCompletedCourses.stream().anyMatch(cc -> {
                RequiredMajorCourse requiredMajorCourse = requiredMajorCourseRepository.findById(status.getRequiredCourseId()).orElseThrow(() -> new RequiredMajorCourseNotFoundException(status.getRequiredCourseId()));
                if(cc.getCourseOffering().getCourse().getId().equals(requiredMajorCourse.getCourse().getId())){
                    return true;
                }
                int year = cc.getCourseOffering().getSemester().getYear();

                return ruleRepository.isCourseTransferable(cc.getCourseOffering().getCourse().getId(),
                        requiredMajorCourse.getCourse().getId(), year);
            });

            if (fulfilled) {
                status.markFulfilled();
            }


        }

        List<UserCompletedCourse> allCompletedCourses = userCompletedCourseRepository.findByUserId(event.getUserId());
        if(allCompletedCourses.isEmpty()){
            throw new UserCompletedCourseNotFoundException(event.getUserId());
        }

        List<UserCompletedCourse> liberalCompletedCourses = allCompletedCourses.stream().filter(ucc -> ucc.getCourseOffering().getCourse().getCourseType().equals(CourseType.GENERAL))
                .toList();
        for(UserRequiredCourseStatus status : userStatuses){
            if(status.isFulfilled() || status.getCourseType() != CourseType.GENERAL) continue;
            // 현재 갱신해야하는 required status 랑 연결된 교양필수요건 찾음
            RequiredLiberalCategoryByCollege requiredLiberalCategory = requiredLiberalCategoryByCollegeRepository.findById(status.getRequiredCourseId()).orElseThrow(() -> new RequiredLiberalCategoryNotFoundException(status.getRequiredCourseId()));
            int requiredCredit = requiredLiberalCategory.getRequiredCredit();
            LiberalCategory liberalCategory = requiredLiberalCategory.getLiberalCategory();

            int earnedCredit = liberalCompletedCourses.stream()
                    .map(all -> all.getCourseOffering().getCourse())
                    .filter( course -> liberalCategoryCourseRepository.existsByCourseIdAndLiberalCategoryId(course.getId(), liberalCategory.getId()))
                    .mapToInt(Course::getCredit)
                    .sum();

            if(requiredCredit <= earnedCredit){
                status.markFulfilled();
            }

        }

    }

}
