package com.hyewon.wiseowl_backend.domain.user.service;

import com.hyewon.wiseowl_backend.domain.auth.repository.RefreshTokenRepository;
import com.hyewon.wiseowl_backend.domain.course.entity.CourseOffering;
import com.hyewon.wiseowl_backend.domain.course.entity.Major;
import com.hyewon.wiseowl_backend.domain.course.entity.Semester;
import com.hyewon.wiseowl_backend.domain.course.service.CourseOfferingQueryService;
import com.hyewon.wiseowl_backend.domain.course.service.MajorQueryService;
import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorRequirement;
import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;
import com.hyewon.wiseowl_backend.domain.requirement.service.CreditRequirementQueryService;
import com.hyewon.wiseowl_backend.domain.user.dto.*;
import com.hyewon.wiseowl_backend.domain.user.entity.*;
import com.hyewon.wiseowl_backend.domain.user.event.*;
import com.hyewon.wiseowl_backend.domain.user.repository.*;
import com.hyewon.wiseowl_backend.global.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final UserMajorRepository userMajorRepository;
    private final UserCompletedCourseRepository userCompletedCourseRepository;
    private final CourseOfferingQueryService courseOfferingQueryService;
    private final UserRequirementStatusRepository userRequirementStatusRepository;
    private final CreditRequirementQueryService creditRequirementQueryService;
    private final UserRequiredCourseStatusRepository userRequiredCourseStatusRepository;
    private final UserTrackRepository userTrackRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final MajorQueryService majorQueryService;

    @Transactional
    public void updateUserProfile(Long userId, ProfileUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException(userId));
        Profile profile = profileRepository.findByUserId(userId).orElseThrow(ProfileNotFoundException::new);
        Integer entranceYear = request.entranceYear();

        user.updateUsername(request.name());
        profile.updateEntranceYear(entranceYear);
        userTrackRepository.save(UserTrack.of(user, request.track()));

        for (UserMajorRequest majorRequest : request.majors()) {
            Major major = majorQueryService.getMajor(majorRequest.majorId());
            userMajorRepository.save(UserMajor.of(user, major, majorRequest.majorType()));
        }
        eventPublisher.publishEvent(new UserMajorRegisteredEvent(userId, request.majors(), entranceYear));
    }

    @CacheEvict(value = "userCompletedCourses", key = "#userId")
    @Transactional
    public void insertCompletedCourses(Long userId, CompletedCourseInsertRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        List<UserCompletedCourse> toSave = request.courses().stream()
                .map(c -> {
                    CourseOffering offering = courseOfferingQueryService.getCourseOffering(c.courseOfferingId());
                    boolean alreadyExists = userCompletedCourseRepository.existsByUserIdAndCourseOffering_Id(userId, c.courseOfferingId());
                    if (alreadyExists) {
                        throw new CompletedCourseAlreadyExistsException();
                    }
                    return UserCompletedCourse.of(user, offering, c.grade(), c.retake());
                }).toList();

        userCompletedCourseRepository.saveAll(toSave);
        eventPublisher.publishEvent(new CompletedCoursesRegisteredEvent(userId, toSave));
    }

    @Cacheable(value = "graduationRequirements", key = "#userId")
    @Transactional(readOnly = true)
    public GraduationRequirementsResponse getGraduationRequirementsForUser(Long userId) {
        List<UserRequirementStatus> all = userRequirementStatusRepository.findAllByUserIdWithMajor(userId);
        if (all.isEmpty()) {
            throw new UserRequirementStatusNotFoundException(userId);
        }

        ArrayList<List<UserRequirementStatus>> grouped = new ArrayList<>(all.stream()
                .collect(Collectors.groupingBy(urs ->
                        urs.getMajorRequirement().getMajor().getId())
                ).values());

        List<GraduationRequirementGroupByMajorResponse> responses = grouped.stream()
                .map(statuses -> {
                    MajorRequirement firstMajorRequirement = statuses.get(0).getMajorRequirement();
                    Long majorId = firstMajorRequirement.getMajor().getId();
                    String majorName = firstMajorRequirement.getMajor().getName();
                    return GraduationRequirementGroupByMajorResponse.from(
                            majorId, majorName, firstMajorRequirement.getMajorType(), statuses
                    );
                }).toList();

        return GraduationRequirementsResponse.from(responses);
    }

    @Caching(evict = {
            @CacheEvict(value = "graduationRequirements", key = "#userId"),
            @CacheEvict(value = "graduationStatus", key = "#userId")
    })
    @Transactional
    public void updateUserRequirementStatus(Long userId, List<UserRequirementFulfillmentRequest> requests) {
        for (UserRequirementFulfillmentRequest update : requests) {
            UserRequirementStatus status = userRequirementStatusRepository.findById(update.userRequirementStatusId())
                    .orElseThrow(() -> new UserRequirementStatusNotFoundException(userId));

            status.updateFulfilled(update.fulfilled());
        }
    }

    @Cacheable(value = "graduationStatus", key = "#userId")
    @Transactional(readOnly = true)
    public MainPageGraduationStatusResponse getUserGraduationOverview(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        List<UserMajor> userMajors = userMajorRepository.findAllByUserIdWithMajor(userId);
        if (userMajors.isEmpty()) {
            throw new UserMajorNotFoundException(userId);
        }

        UserMajor primaryMajor = userMajors.stream()
                .filter(um -> um.getMajorType() == MajorType.PRIMARY)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No PRIMARY major found"));

        Major primaryMajorEntity = primaryMajor.getMajor();

        UserTrack userTrack = userTrackRepository.findByUserId(userId);

        List<RequirementStatusByMajor> requirementStatus = userMajors.stream()
                .map(userMajor -> {
                    Major major = userMajor.getMajor();
                    MajorType majorType = userMajor.getMajorType();

                    List<UserRequirementStatus> statuses = userRequirementStatusRepository.findByUserAndMajor(userId, major, majorType);

                    int requiredCredits = creditRequirementQueryService.sumRequiredCredits(primaryMajorEntity, majorType, userTrack.getTrack(), user.getProfile().getEntranceYear());
                    int earnedCredits = userCompletedCourseRepository.sumCreditsByUserAndMajor(userId, major.getId());

                    return RequirementStatusByMajor.from(major.getName(), userMajor.getMajorType(), earnedCredits, requiredCredits, statuses);
                }).toList();

        return new MainPageGraduationStatusResponse(user.getUsername(), requirementStatus);
    }

    @Cacheable(value = "userRequiredCourseStatus", key = "#userId + ':' + #majorType")
    @Transactional(readOnly = true)
    public UserRequiredCourseStatusResponse getUserRequiredCourseStatus(Long userId, MajorType majorType) {
        List<MajorRequiredCourseItemResponse> majorRequired = userRequiredCourseStatusRepository.findMajorItems(userId, majorType);
        List<LiberalRequiredCourseItemResponse> liberalRequired = userRequiredCourseStatusRepository.findLiberalItems(userId);

        return new UserRequiredCourseStatusResponse(majorRequired, liberalRequired);
    }

    @Cacheable(value = "userSummary", key = "#userId")
    @Transactional(readOnly = true)
    public UserSummaryResponse getUserSummary(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        Profile profile = user.getProfile();

        UserTrack userTrack = userTrackRepository.findByUserId(userId);

        UserMajorDetail firstMajorDetail = userMajorRepository.findUserMajorWithCollege(userId, MajorType.PRIMARY).orElse(null);
        UserMajorDetail secondMajorDetail = null;

        boolean isDouble = userMajorRepository.existsByUserIdAndMajorType(userId, MajorType.DOUBLE);
        boolean isMinor = userMajorRepository.existsByUserIdAndMajorType(userId, MajorType.MINOR);

        if (isDouble) {
            secondMajorDetail = userMajorRepository.findUserMajorWithCollege(userId, MajorType.DOUBLE).orElse(null);
        } else if (isMinor) {
            secondMajorDetail = userMajorRepository.findUserMajorWithCollege(userId, MajorType.MINOR).orElse(null);
        }

        return new UserSummaryResponse(user.getUsername(), profile.getEntranceYear(), profile.getGpa(),
                firstMajorDetail, secondMajorDetail, userTrack.getTrack());
    }

    @Caching(evict = {
            @CacheEvict(value = "userSummary", key = "#userId"),
            @CacheEvict(value = "userRequiredCourseStatus", key = "#userId + ':' + #majorType"),
            @CacheEvict(value = "graduationStatus", key = "#userId"),
            @CacheEvict(value = "graduationRequirements", key = "#userId")
    })
    @Transactional
    public void updateUserMajor(Long userId, List<UserMajorUpdateRequest> requests) {
        requests.forEach(
                request -> {
                    UserMajor userMajor = userMajorRepository.findByUserIdAndMajorType(userId, request.majorType());
                    Major major = majorQueryService.getMajor(request.newMajorId());
                    userMajor.updateMajor(major);
                }
        );

        eventPublisher.publishEvent(new UserMajorUpdateEvent(userId, requests));
    }

    @Caching(evict = {
            @CacheEvict(value = "userSummary", key = "#userId"),
            @CacheEvict(value = "userRequiredCourseStatus", key = "#userId + ':' + #majorType"),
            @CacheEvict(value = "graduationStatus", key = "#userId"),
            @CacheEvict(value = "graduationRequirements", key = "#userId")
    })
    @Transactional
    public void updateUserMajorTypes(Long userId, UserMajorTypeUpdateRequest request) {
        request.userMajorTypeUpdateItems().forEach(item -> {
            UserMajor userMajor = userMajorRepository.findById(item.userMajorId())
                    .orElseThrow(() -> new UserMajorNotFoundException(item.userMajorId()));
            userMajor.updateMajorType(item.newMajorType());
        });

        UserTrack userTrack = userTrackRepository.findByUserId(userId);
        userTrack.updateTrack(request.track());

        eventPublisher.publishEvent(new UserMajorTypeUpdateEvent(userId, request.userMajorTypeUpdateItems()));
    }

    @Cacheable(value = "userCompletedCourses", key = "#userId")
    @Transactional(readOnly = true)
    public UserCompletedCoursesResponse getUserCompletedCourses(Long userId) {
        List<UserCompletedCourse> completedCourses = userCompletedCourseRepository.findAllByUserIdWithCourseOffering(userId);

        Map<Long, List<UserCompletedCourse>> grouped =
                completedCourses.stream()
                        .collect(Collectors.groupingBy(c -> c.getCourseOffering().getSemester().getId()));

        List<UserCompletedCourseBySemesterResponse> responses = grouped.entrySet().stream()
                .map(entry -> {
                    Semester semester = entry.getValue().get(0).getCourseOffering().getSemester();
                    return new UserCompletedCourseBySemesterResponse(
                            semester.getId(),
                            semester.getYear(),
                            semester.getTerm(),
                            entry.getValue().stream()
                                    .map(UserCompletedCourseItem::from)
                                    .toList()
                    );
                })
                .toList();

        return UserCompletedCoursesResponse.from(responses);
    }

    @Caching(evict = {
            @CacheEvict(value = "userCompletedCourses", key = "#userId"),
            @CacheEvict(value = "userRequiredCourseStatus", key = "#userId + ':' + #majorType"),
            @CacheEvict(value = "graduationStatus", key = "#userId")
    })
    @Transactional
    public void updateCompletedCourses(Long userId, List<CompletedCourseUpdateRequest> requests) {
        requests.forEach(request -> {
            UserCompletedCourse userCompletedCourse = userCompletedCourseRepository.findById(request.userCompletedCourseId()).orElseThrow(() -> new UserCompletedCourseNotFoundException(request.userCompletedCourseId()));
            if (request.grade() != null) {
                userCompletedCourse.updateGrade(request.grade());
            }

            if (request.retake() != null) {
                userCompletedCourse.updateRetake(request.retake());
            }
        });
        eventPublisher.publishEvent(new CompletedCoursesUpdateEvent(userId));
    }

    @Caching(evict = {
            @CacheEvict(value = "userCompletedCourses", key = "#userId"),
            @CacheEvict(value = "userRequiredCourseStatus", key = "#userId + ':' + #majorType"),
            @CacheEvict(value = "graduationStatus", key = "#userId")
    })
    @Transactional
    public void deleteCompletedCourse(Long userId, Long userCompletedCourseId) {
        UserCompletedCourse course = userCompletedCourseRepository.findById(userCompletedCourseId)
                .orElseThrow(() -> new UserCompletedCourseNotFoundException(userCompletedCourseId));

        userCompletedCourseRepository.delete(course);

        eventPublisher.publishEvent(new CompletedCoursesUpdateEvent(userId));
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        refreshTokenRepository.deleteByEmail(user.getEmail());
        userRepository.delete(user);
    }
}
