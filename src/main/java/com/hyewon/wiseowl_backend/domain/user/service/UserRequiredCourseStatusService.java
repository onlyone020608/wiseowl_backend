package com.hyewon.wiseowl_backend.domain.user.service;

import com.hyewon.wiseowl_backend.domain.course.entity.CourseType;
import com.hyewon.wiseowl_backend.domain.course.entity.LiberalCategory;
import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;
import com.hyewon.wiseowl_backend.domain.requirement.entity.RequiredLiberalCategory;
import com.hyewon.wiseowl_backend.domain.requirement.entity.RequiredMajorCourse;
import com.hyewon.wiseowl_backend.domain.requirement.service.CourseCreditTransferRuleService;
import com.hyewon.wiseowl_backend.domain.requirement.service.RequiredLiberalCategoryQueryService;
import com.hyewon.wiseowl_backend.domain.requirement.service.RequiredMajorCourseQueryService;
import com.hyewon.wiseowl_backend.domain.user.entity.Profile;
import com.hyewon.wiseowl_backend.domain.user.entity.User;
import com.hyewon.wiseowl_backend.domain.user.entity.UserCompletedCourse;
import com.hyewon.wiseowl_backend.domain.user.entity.UserRequiredCourseStatus;
import com.hyewon.wiseowl_backend.domain.user.repository.*;
import com.hyewon.wiseowl_backend.global.exception.ProfileNotFoundException;
import com.hyewon.wiseowl_backend.global.exception.UserNotFoundException;
import com.hyewon.wiseowl_backend.global.exception.UserRequiredCourseStatusNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserRequiredCourseStatusService {
    private final UserRequiredCourseStatusRepository userRequiredCourseStatusRepository;
    private final CourseCreditTransferRuleService courseCreditTransferRuleService;
    private final UserCompletedCourseRepository userCompletedCourseRepository;
    private final RequiredMajorCourseQueryService requiredMajorCourseQueryService;
    private final UserRepository userRepository;
    private final RequiredLiberalCategoryQueryService requiredLiberalCategoryQueryService;
    private final UserMajorRepository userMajorRepository;
    private final ProfileRepository profileRepository;

    @Transactional
    public void updateUserRequiredCourseStatus(Long userId, List<UserCompletedCourse> completedCourses) {
        List<UserRequiredCourseStatus> userStatuses = userRequiredCourseStatusRepository.findAllByUserId(userId);
        if (userStatuses.isEmpty()) {
            throw new UserRequiredCourseStatusNotFoundException(userId);
        }

        updateMajorCourseStatuses(completedCourses, userStatuses);
        updateLiberalCourseStatuses(userId, userStatuses);
    }

    private void updateLiberalCourseStatuses(Long userId, List<UserRequiredCourseStatus> userStatuses) {
        for (UserRequiredCourseStatus status : userStatuses) {
            if (status.isFulfilled() || status.getCourseType() != CourseType.GENERAL) continue;

            // 현재 갱신해야하는 required status 랑 연결된 교양필수요건 찾음
            RequiredLiberalCategory requiredLiberalCategory = requiredLiberalCategoryQueryService.getRequiredLiberalWithCategory(status.getRequiredCourseId());

            int requiredCredit = requiredLiberalCategory.getRequiredCredit();
            LiberalCategory liberalCategory = requiredLiberalCategory.getLiberalCategory();

            int earnedCredit = userCompletedCourseRepository.sumCreditsByUserAndLiberalCategory(userId, liberalCategory.getId());

            if (requiredCredit <= earnedCredit) {
                status.markFulfilled();
            }
        }
    }

    private void updateMajorCourseStatuses(List<UserCompletedCourse> completedCourses, List<UserRequiredCourseStatus> userStatuses) {
        List<UserCompletedCourse> majorCompletedCourses = completedCourses.stream()
                .filter(cc -> cc.getCourseOffering().getCourse().getCourseType() == CourseType.MAJOR)
                .toList();

        for (UserRequiredCourseStatus status : userStatuses) {
            if (status.isFulfilled() || status.getCourseType() != CourseType.MAJOR) continue;

            boolean fulfilled = majorCompletedCourses.stream().anyMatch(cc -> {
                RequiredMajorCourse requiredMajorCourse = requiredMajorCourseQueryService.getRequiredMajorCourseWithCourse(status.getRequiredCourseId());

                if (requiredMajorCourseQueryService.isCourseMatched(status.getRequiredCourseId(), cc.getId())) {
                    return true;
                }

                return courseCreditTransferRuleService.isTransferable(cc, requiredMajorCourse);
            });

            if (fulfilled) {
                status.markFulfilled();
            }
        }
    }

    @Transactional
    public void replaceUserRequiredCourseStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        Profile profile = profileRepository.findByUserId(userId).orElseThrow(ProfileNotFoundException::new);

        userRequiredCourseStatusRepository.deleteAllByUserId(userId);

        userMajorRepository.findAllByUserIdWithMajorAndCollege(userId).forEach(userMajor -> {
            List<RequiredMajorCourse> majorCourseList= requiredMajorCourseQueryService.getApplicableMajorCourses(userMajor.getMajor().getId(),
                    userMajor.getMajorType(), profile.getEntranceYear());
            List<UserRequiredCourseStatus> requiredMajorCourseStatuses = majorCourseList.stream()
                    .map(requiredMajorCourse -> UserRequiredCourseStatus.of(user, CourseType.MAJOR, requiredMajorCourse.getId()))
                    .toList();

            userRequiredCourseStatusRepository.saveAll(requiredMajorCourseStatuses);

            // primary major에 해당할 때만
            if (userMajor.getMajorType().equals(MajorType.PRIMARY)) {
                List<UserRequiredCourseStatus> requiredLiberalCourseStatuses = requiredLiberalCategoryQueryService.getApplicableLiberalCategories(userMajor.getMajor().getId(),
                                profile.getEntranceYear())
                        .stream()
                        .map(requiredLiberal -> UserRequiredCourseStatus.of(user, CourseType.GENERAL, requiredLiberal.getId()))
                        .toList();

                userRequiredCourseStatusRepository.saveAll(requiredLiberalCourseStatuses);
            }
        });
    }
}
