package com.hyewon.wiseowl_backend.domain.user.service;

import com.hyewon.wiseowl_backend.domain.course.entity.*;
import com.hyewon.wiseowl_backend.domain.course.service.CourseOfferingQueryService;
import com.hyewon.wiseowl_backend.domain.course.service.MajorQueryService;
import com.hyewon.wiseowl_backend.domain.requirement.entity.*;
import com.hyewon.wiseowl_backend.domain.requirement.service.CreditRequirementQueryService;
import com.hyewon.wiseowl_backend.domain.requirement.service.MajorRequirementQueryService;
import com.hyewon.wiseowl_backend.domain.requirement.service.RequiredLiberalCategoryQueryService;
import com.hyewon.wiseowl_backend.domain.requirement.service.RequiredMajorCourseQueryService;
import com.hyewon.wiseowl_backend.domain.user.dto.*;
import com.hyewon.wiseowl_backend.domain.user.entity.*;
import com.hyewon.wiseowl_backend.domain.user.event.CompletedCoursesRegisteredEvent;
import com.hyewon.wiseowl_backend.domain.user.event.CompletedCoursesUpdateEvent;
import com.hyewon.wiseowl_backend.domain.user.repository.*;
import com.hyewon.wiseowl_backend.global.exception.*;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    private final MajorRequirementQueryService majorRequirementQueryService;
    private final UserRequirementStatusRepository userRequirementStatusRepository;
    private final CreditRequirementQueryService creditRequirementQueryService;
    private final RequiredMajorCourseQueryService requiredMajorCourseQueryService;
    private final RequiredLiberalCategoryQueryService requiredLiberalCategoryQueryService;
    private final UserRequiredCourseStatusRepository userRequiredCourseStatusRepository;
    private final UserTrackRepository userTrackRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final EntityManager entityManager;
    private final MajorQueryService majorQueryService;

    @Transactional
    public void updateUserProfile(Long userId, ProfileUpdateRequest request){
        User user = userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException(userId));
        Profile profile = profileRepository.findByUserId(userId).orElseThrow(ProfileNotFoundException::new);
        Integer entranceYear = request.entranceYear();

        user.updateUsername(request.name());
        profile.updateEntranceYear(entranceYear);
        userTrackRepository.save(UserTrack.of(user, request.track()));

        for (UserMajorRequest majorRequest : request.majors()) {
            Major major = majorQueryService.getMajor(majorRequest.majorId());
            userMajorRepository.save(UserMajor.of(user, major, majorRequest.majorType()));

            setupRequirementStatusesForMajor(majorRequest, entranceYear, major, user);
            setupRequiredCourseStatusesForMajor(majorRequest, entranceYear, user);

            // primary major에 해당할 때만
            if(majorRequest.majorType().equals(MajorType.PRIMARY)){
                List<UserRequiredCourseStatus> requiredLiberalCourseStatuses = requiredLiberalCategoryQueryService.getApplicableLiberalCategories(major.getCollege().getId(), entranceYear)
                        .stream()
                        .map(requiredLiberal -> UserRequiredCourseStatus.of(user, CourseType.GENERAL, requiredLiberal.getId()))
                        .toList();

                userRequiredCourseStatusRepository.saveAll(requiredLiberalCourseStatuses);
            }
        }
    }

    private void setupRequiredCourseStatusesForMajor(UserMajorRequest majorRequest, Integer entranceYear, User user) {
        List<RequiredMajorCourse> majorCourseList= requiredMajorCourseQueryService.getApplicableMajorCourses(majorRequest.majorId(), majorRequest.majorType(), entranceYear);
        List<UserRequiredCourseStatus> requiredMajorCourseStatuses = majorCourseList.stream()
                .map(requiredMajorCourse -> UserRequiredCourseStatus.of(user, CourseType.MAJOR, requiredMajorCourse.getId()))
                .toList();

        userRequiredCourseStatusRepository.saveAll(requiredMajorCourseStatuses);
    }

    private void setupRequirementStatusesForMajor(UserMajorRequest majorRequest,Integer entranceYear, Major major, User user) {
        List<MajorRequirement> applicable = majorRequirementQueryService.getApplicableRequirements(major.getId(), majorRequest.majorType(), entranceYear);
        List<UserRequirementStatus> toSave = applicable.stream()
                .map(req -> UserRequirementStatus.of(user, req))
                .toList();

        userRequirementStatusRepository.saveAll(toSave);
    }

    @Transactional
    public void insertCompletedCourses(Long userId, CompletedCourseInsertRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        boolean alreadyExists = userCompletedCourseRepository.existsByUserId(userId);
        if (alreadyExists) {
            throw new CompletedCourseAlreadyExistsException();
        }

        List<UserCompletedCourse> toSave = request.courses().stream()
                .map(c -> {
                    CourseOffering offering = courseOfferingQueryService.getCourseOffering(c.courseOfferingId());
                    return UserCompletedCourse.of(user, offering, c.grade(), c.retake());
                }).toList();

        userCompletedCourseRepository.saveAll(toSave);
        eventPublisher.publishEvent(new CompletedCoursesRegisteredEvent(userId, toSave));
    }

    @Transactional(readOnly = true)
    public List<GraduationRequirementGroupByMajorResponse> getGraduationRequirementsForUser(Long userId){
        List<UserRequirementStatus> all = userRequirementStatusRepository.findAllByUserId(userId);
        if(all.isEmpty()){
            throw new UserRequirementStatusNotFoundException(userId);
        }

        ArrayList<List<UserRequirementStatus>> grouped = new ArrayList<>(all.stream()
                .collect(Collectors.groupingBy(urs ->
                        urs.getMajorRequirement().getMajor().getId())
                ).values());

        return grouped.stream().map(
                statuses -> {
                    MajorRequirement firstMajorRequirement = statuses.get(0).getMajorRequirement();
                    Long majorId = firstMajorRequirement.getMajor().getId();
                    String majorName = firstMajorRequirement.getMajor().getName();
                    return GraduationRequirementGroupByMajorResponse.from(
                            majorId, majorName, firstMajorRequirement.getMajorType(), statuses
                    );
                }).toList();
    }

    @Transactional
    public void updateUserRequirementStatus(Long userId, UserRequirementFulfillmentRequest request){
        for(RequirementStatusUpdate update : request.requirements()){
            UserRequirementStatus status = userRequirementStatusRepository.findById(update.userRequirementStatusId())
                    .orElseThrow(() -> new UserRequirementStatusNotFoundException(userId));

            status.updateFulfilled(update.fulfilled());
        }
    }

    @Transactional(readOnly = true)
    public MainPageGraduationStatusResponse getUserGraduationOverview(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        List<UserMajor> userMajors = userMajorRepository.findAllByUserId(userId);
        if (userMajors.isEmpty()) {
            throw new UserMajorNotFoundException(userId);
        }

        // TODO: 미정인 1학년의 경우 처리
        UserTrack userTrack = userTrackRepository.findByUserId(userId);

        List<RequirementStatusByMajor> requirementStatus = userMajors.stream()
                .map(userMajor -> {
                    Major major = userMajor.getMajor();
                    MajorType majorType = userMajor.getMajorType();

                    List<UserRequirementStatus> statuses = userRequirementStatusRepository.findByUserAndMajor(userId, major, majorType);

                    int requiredCredits = creditRequirementQueryService.sumRequiredCredits(major, majorType, userTrack.getTrack(), user.getProfile().getEntranceYear());
                    int earnedCredits = userCompletedCourseRepository.sumCreditsByUser(userId);

                    return RequirementStatusByMajor.from(major.getName(), earnedCredits, requiredCredits, statuses);
                }).toList();

        return new MainPageGraduationStatusResponse(user.getUsername(), requirementStatus);
    }

    @Transactional(readOnly = true)
    public UserRequiredCourseStatusResponse getUserRequiredCourseStatus(Long userId, MajorType majorType) {
        List<MajorRequiredCourseItemResponse> majorRequired = userRequiredCourseStatusRepository.findMajorItems(userId, majorType);
        List<LiberalRequiredCourseItemResponse> liberalRequired = userRequiredCourseStatusRepository.findLiberalItems(userId);

        return new UserRequiredCourseStatusResponse(majorRequired, liberalRequired);
    }

    @Transactional(readOnly = true)
    public List<UserGraduationRequirementStatusResponse> fetchUserGraduationRequirementStatus(Long userId){
        List<UserRequirementStatus> requirementStatuses = userRequirementStatusRepository.findAllByUserId(userId);
        if(requirementStatuses.isEmpty()) {
            throw new UserRequirementStatusNotFoundException(userId);
        }
        Map<MajorType, List<UserRequirementStatus>> map = requirementStatuses.stream()
                .collect(Collectors.groupingBy(urs -> urs.getMajorRequirement().getMajorType()));

        return map.entrySet().stream().map(
                entry -> {
                    MajorType type = entry.getKey();
                    List<UserRequirementStatus> statuses = entry.getValue();
                    List<GraduationRequirementItemResponse> graduationRequirementItems = statuses.stream().map(
                            status -> {
                                String name = status.getMajorRequirement().getRequirement().getName();
                                String description = status.getMajorRequirement().getDescription();
                                return new GraduationRequirementItemResponse(name, description, status.isFulfilled());
                            }
                    ).toList();
                    return new UserGraduationRequirementStatusResponse(type, graduationRequirementItems);

                }
        ).toList();
    }

    @Transactional(readOnly = true)
    public UserSummaryResponse fetchUserSummary(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        Profile profile = user.getProfile();

        UserMajor primaryUserMajor = userMajorRepository.findByUserIdAndMajorType(userId, MajorType.PRIMARY);
        Optional<UserMajor> secondUserMajor = userMajorRepository.findByUserIdAndMajorTypeIn(
                userId, List.of(MajorType.DOUBLE, MajorType.MINOR)
        );

        Major primaryMajor = primaryUserMajor.getMajor();
        College primaryCollege = primaryMajor.getCollege();
        UserMajorDetail secondMajorDetail = secondUserMajor
                .map(UserMajor::getMajor)
                .map(major -> {
                    College college = major.getCollege();
                    return new UserMajorDetail(
                            college.getId(),
                            college.getName(),
                            major.getId(),
                            major.getName()
                    );
                })
                .orElse(null);

        return new UserSummaryResponse(user.getUsername(), user.getStudentId(),profile.getGPA(),
                new UserMajorDetail(primaryCollege.getId(), primaryCollege.getName(), primaryMajor.getId(), primaryMajor.getName())
                ,secondMajorDetail);
    }

    @Transactional
    public void updateUserMajor(Long userId, List<UserMajorUpdateRequest> requests){
        requests.forEach(
                request -> {
                    UserMajor userMajor = userMajorRepository.findByUserIdAndMajorType(userId, request.majorType());
                    Major major = majorQueryService.getMajor(request.majorId());
                    userMajor.updateMajor(major);
                }
        );
    }

    @Transactional
    public void updateUserMajorTypes(List<UserMajorTypeUpdateRequest> requests) {
        requests.forEach(request -> {
            UserMajor userMajor = userMajorRepository.findById(request.userMajorId())
                    .orElseThrow(() -> new UserMajorNotFoundException(request.userMajorId()));
            userMajor.updateMajorType(request.majorType());
        });
    }

    @Transactional
    public void updateCompletedCourses(Long userId, List<CompletedCourseUpdateRequest> requests){
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

    @Transactional
    public void registerUserSubscriptions(Long userId, List<UserSubscriptionRequest> requests){
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        List<UserSubscription> toSave = requests.stream().map(
                request -> {
                    return UserSubscription.of(user, request.targetId(), request.type());
                }
        ).toList();
        userSubscriptionRepository.saveAll(toSave);
    }

    @Transactional
    public void replaceAllUserSubscriptions(Long userId,  List<UserSubscriptionRequest> requests){
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        userSubscriptionRepository.deleteByUserId(userId);
        entityManager.flush();

        List<UserSubscription> toSave = requests.stream().map(
                request -> {
                    return UserSubscription.of(user, request.targetId(), request.type());
                }
        ).toList();

        userSubscriptionRepository.saveAll(toSave);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.markAsDeleted();
    }
}
