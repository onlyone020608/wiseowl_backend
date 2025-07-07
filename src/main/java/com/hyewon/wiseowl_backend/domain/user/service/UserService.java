package com.hyewon.wiseowl_backend.domain.user.service;

import com.hyewon.wiseowl_backend.domain.course.entity.CourseOffering;
import com.hyewon.wiseowl_backend.domain.course.entity.Major;
import com.hyewon.wiseowl_backend.domain.course.repository.CourseOfferingRepository;
import com.hyewon.wiseowl_backend.domain.course.repository.MajorRepository;
import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorRequirement;
import com.hyewon.wiseowl_backend.domain.requirement.repository.MajorRequirementRepository;
import com.hyewon.wiseowl_backend.domain.user.dto.CompletedCourseUpdateRequest;
import com.hyewon.wiseowl_backend.domain.user.dto.ProfileUpdateRequest;
import com.hyewon.wiseowl_backend.domain.user.dto.UserMajorRequest;
import com.hyewon.wiseowl_backend.domain.user.entity.*;
import com.hyewon.wiseowl_backend.domain.user.repository.*;
import com.hyewon.wiseowl_backend.global.exception.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final UserMajorRepository userMajorRepository;
    private final MajorRepository majorRepository;
    private final UserCompletedCourseRepository userCompletedCourseRepository;
    private final CourseOfferingRepository courseOfferingRepository;
    private final MajorRequirementRepository majorRequirementRepository;
    private final UserRequirementStatusRepository userRequirementStatusRepository;



    @Transactional
    public void updateUserProfile(Long userId, ProfileUpdateRequest request){
        User user = userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException(userId));
        Profile profile = profileRepository.findByUserId(userId).orElseThrow(ProfileNotFoundException::new);

        user.updateUsername(request.name());
        profile.updateEntranceYear(request.entranceYear());

        for (UserMajorRequest majorRequest : request.majors()) {
            Major major = majorRepository.findById(majorRequest.majorId())
                    .orElseThrow(() -> new MajorNotFoundException(majorRequest.majorId()));

            UserMajor userMajor = UserMajor.of(user, major, majorRequest.majorType());
            userMajorRepository.save(userMajor);

            List<MajorRequirement> applicable = majorRequirementRepository.findApplicable(major.getId(), majorRequest.majorType(), request.entranceYear());
            List<UserRequirementStatus> toSave = applicable.stream()
                    .map(req -> UserRequirementStatus.of(user, req))
                    .toList();

            userRequirementStatusRepository.saveAll(toSave);


        }

    }

    @Transactional
    public void insertCompletedCourses(Long userId, CompletedCourseUpdateRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean alreadyExists = userCompletedCourseRepository.existsByUserId(userId);
        if (alreadyExists) {
            throw new CompletedCourseAlreadyExistsException();
        }
        List<UserCompletedCourse> toSave = request.courses().stream()
                .map(c -> {
                    CourseOffering offering = courseOfferingRepository.findById(c.courseOfferingId())
                            .orElseThrow(() -> new CourseOfferingNotFoundException(c.courseOfferingId()));

                    return UserCompletedCourse.of(user, offering, c.grade(), c.retake());
                }).toList();

        userCompletedCourseRepository.saveAll(toSave);


    }
}
