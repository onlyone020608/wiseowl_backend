package com.hyewon.wiseowl_backend.domain.user.service;

import com.hyewon.wiseowl_backend.domain.course.entity.CourseOffering;
import com.hyewon.wiseowl_backend.domain.course.entity.Major;
import com.hyewon.wiseowl_backend.domain.course.repository.CourseOfferingRepository;
import com.hyewon.wiseowl_backend.domain.course.repository.MajorRepository;
import com.hyewon.wiseowl_backend.domain.user.dto.CompletedCourseUpdateItem;
import com.hyewon.wiseowl_backend.domain.user.dto.CompletedCourseUpdateRequest;
import com.hyewon.wiseowl_backend.domain.user.dto.ProfileUpdateRequest;
import com.hyewon.wiseowl_backend.domain.user.dto.UserMajorRequest;
import com.hyewon.wiseowl_backend.domain.user.entity.Profile;
import com.hyewon.wiseowl_backend.domain.user.entity.User;
import com.hyewon.wiseowl_backend.domain.user.entity.UserCompletedCourse;
import com.hyewon.wiseowl_backend.domain.user.entity.UserMajor;
import com.hyewon.wiseowl_backend.domain.user.repository.ProfileRepository;
import com.hyewon.wiseowl_backend.domain.user.repository.UserCompletedCourseRepository;
import com.hyewon.wiseowl_backend.domain.user.repository.UserMajorRepository;
import com.hyewon.wiseowl_backend.domain.user.repository.UserRepository;
import com.hyewon.wiseowl_backend.global.exception.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
