package com.hyewon.wiseowl_backend.domain.user.service;

import com.hyewon.wiseowl_backend.domain.course.entity.Course;
import com.hyewon.wiseowl_backend.domain.course.entity.CourseType;
import com.hyewon.wiseowl_backend.domain.requirement.entity.*;
import com.hyewon.wiseowl_backend.domain.requirement.repository.CreditRequirementRepository;
import com.hyewon.wiseowl_backend.domain.requirement.repository.RequiredLiberalCategoryByCollegeRepository;
import com.hyewon.wiseowl_backend.domain.requirement.repository.RequiredMajorCourseRepository;
import com.hyewon.wiseowl_backend.domain.user.dto.*;
import com.hyewon.wiseowl_backend.domain.course.entity.CourseOffering;
import com.hyewon.wiseowl_backend.domain.course.entity.Major;
import com.hyewon.wiseowl_backend.domain.course.repository.CourseOfferingRepository;
import com.hyewon.wiseowl_backend.domain.course.repository.MajorRepository;
import com.hyewon.wiseowl_backend.domain.requirement.repository.MajorRequirementRepository;
import com.hyewon.wiseowl_backend.domain.user.entity.*;
import com.hyewon.wiseowl_backend.domain.user.event.CompletedCoursesRegisteredEvent;
import com.hyewon.wiseowl_backend.domain.user.repository.*;
import com.hyewon.wiseowl_backend.global.exception.*;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final MajorRepository majorRepository;
    private final UserCompletedCourseRepository userCompletedCourseRepository;
    private final CourseOfferingRepository courseOfferingRepository;
    private final MajorRequirementRepository majorRequirementRepository;
    private final UserRequirementStatusRepository userRequirementStatusRepository;
    private final CreditRequirementRepository creditRequirementRepository;
    private final RequiredMajorCourseRepository requiredMajorCourseRepository;
    private final RequiredLiberalCategoryByCollegeRepository requiredLiberalCategoryByCollegeRepository;
    private final UserRequiredCourseStatusRepository userRequiredCourseStatusRepository;
    private final ApplicationEventPublisher eventPublisher;




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

            List<RequiredMajorCourse> majorCourseList= requiredMajorCourseRepository.findApplicableMajorCourses(majorRequest.majorId(), majorRequest.majorType(), request.entranceYear());
            List<UserRequiredCourseStatus> requiredMajorCourseStatuses = majorCourseList.stream()
                    .map(requiredMajorCourse -> UserRequiredCourseStatus.of(user, CourseType.MAJOR, requiredMajorCourse.getId()))
                    .toList();

            userRequiredCourseStatusRepository.saveAll(requiredMajorCourseStatuses);

            // primary major에 해당할 때만
            if(majorRequest.majorType().equals(MajorType.PRIMARY)){
                List<UserRequiredCourseStatus> requiredLiberalCourseStatuses = requiredLiberalCategoryByCollegeRepository.findApplicableLiberalCategories(major.getCollege().getId(), request.entranceYear())
                        .stream()
                        .map(requiredLiberal -> UserRequiredCourseStatus.of(user, CourseType.GENERAL, requiredLiberal.getId()))
                        .toList();

                userRequiredCourseStatusRepository.saveAll(requiredLiberalCourseStatuses);


            }






        }

    }

    @Transactional
    public void insertCompletedCourses(Long userId, CompletedCourseInsertRequest request){
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
        eventPublisher.publishEvent(new CompletedCoursesRegisteredEvent(userId, toSave));


    }

    @Transactional(readOnly = true)
    public List<GraduationRequirementGroupByMajorResponse> getGraduationRequirementsForUser(Long userId){
        List<UserRequirementStatus> all = userRequirementStatusRepository.findAllByUserId(userId);
        Map<Long, List<UserRequirementStatus>> grouped = all.stream()
                .collect(Collectors.groupingBy(urs ->
                        urs.getMajorRequirement().getMajor().getId())
                );
        if(all.isEmpty()){
            throw new UserGraduationStatusNotFoundException(userId);
        }

        return grouped.values().stream().map(
                statuses -> {
                    MajorRequirement anyMr = statuses.get(0).getMajorRequirement();
                    Long majorId = anyMr.getMajor().getId();
                    String majorName = anyMr.getMajor().getName();
                    return GraduationRequirementGroupByMajorResponse.from(
                            majorId, majorName, anyMr.getMajorType(), statuses
                    );
                }).toList();



    }

    @Transactional
    public void updateUserRequirementStatus(Long userId, UserRequirementFulfillmentRequest request){
        for(RequirementStatusUpdate update : request.requirements()){
            UserRequirementStatus status = userRequirementStatusRepository.findById(update.userRequirementStatusId())
                    .orElseThrow(() -> new UserGraduationStatusNotFoundException(userId));

            status.updateFulfilled(update.fulfilled());


        }


    }

    @Transactional(readOnly = true)
    public MainPageGraduationStatusResponse fetchUserGraduationOverview(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        List<UserMajor> userMajors = userMajorRepository.findAllByUserId(userId);
        if (userMajors.isEmpty()) {
            throw new UserMajorNotFoundException(userId);
        }
        List<RequirementStatusByMajor> requirementStatus = userMajors.stream()
                .map(userMajor -> {
                    Major major = userMajor.getMajor();
                    MajorType majorType = userMajor.getMajorType();

                    List<UserRequirementStatus> statuses = userRequirementStatusRepository.findAllByUserId(userId)
                            .stream()
                            .filter(urs -> {
                                MajorRequirement mr = urs.getMajorRequirement();
                                return mr.getMajor().equals(major) && mr.getMajorType().equals(majorType);
                            }).toList();

                    List<RequirementStatusSummary> requirements = statuses.stream()
                            .map(status -> new RequirementStatusSummary(
                                    status.getId(), status.getMajorRequirement().getRequirement().getName(),
                                    status.isFulfilled()
                            ))
                            .toList();
                    List<CreditRequirement> creditRequirements =
                            creditRequirementRepository.findAllByMajorIdAndMajorType(major.getId(), majorType);

                    if (creditRequirements.isEmpty()) {
                        throw new CreditRequirementNotFoundException(major.getId(), majorType);
                    }

                    int requiredCredits = creditRequirements.stream()
                            .filter(cr -> cr.getCourseType().equals(CourseType.MAJOR))
                            .mapToInt(CreditRequirement::getRequiredCredits)
                            .sum();

                    int earnedCredits = userCompletedCourseRepository.findByUserId(userId)
                            .stream()
                            .filter(ucc -> ucc.getCourseOffering().getCourse().getCourseType().equals(CourseType.MAJOR))
                            .mapToInt(ucc -> ucc.getCourseOffering().getCourse().getCredit())
                            .sum();

                    return new RequirementStatusByMajor(major.getName(), earnedCredits, requiredCredits, requirements);

                }).toList();

        return new MainPageGraduationStatusResponse(user.getUsername(), requirementStatus);


    }
    @Transactional(readOnly = true)
    public UserRequiredCourseStatusResponse fetchUserRequiredCourseStatus(Long userId, MajorType majorType) {
        List<UserRequiredCourseStatus> userRequiredCourseStatuses = userRequiredCourseStatusRepository.findAllByUserId(userId);
        if(userRequiredCourseStatuses.isEmpty()) {
            throw new  UserRequiredCourseStatusNotFoundException(userId);
        }
        Map<CourseType, List<UserRequiredCourseStatus>> grouped = userRequiredCourseStatuses.stream()
                .collect(Collectors.groupingBy(UserRequiredCourseStatus::getCourseType));
        List<UserRequiredCourseStatus> majorRequiredCourses = grouped.getOrDefault(CourseType.MAJOR, List.of()).stream().filter(
                status -> {
                    RequiredMajorCourse requiredMajorCourse = requiredMajorCourseRepository.findById(status.getRequiredCourseId()).orElseThrow(() -> new RequiredMajorCourseNotFoundException(status.getRequiredCourseId()));
                    return requiredMajorCourse.getMajorType().equals(majorType);
                }
        ).toList();

        List<MajorRequiredCourseItemResponse> majorRequired = majorRequiredCourses.stream().map(
                status -> {
                    RequiredMajorCourse requiredMajorCourse = requiredMajorCourseRepository.findById(status.getRequiredCourseId()).orElseThrow(() -> new RequiredMajorCourseNotFoundException(status.getRequiredCourseId()));
                    Course course = requiredMajorCourse.getCourse();
                    return new MajorRequiredCourseItemResponse(course.getCourseCodePrefix(), course.getName(), status.isFulfilled());
                }
        ).toList();


        List<UserRequiredCourseStatus> liberalRequiredCourses = grouped.getOrDefault(CourseType.GENERAL, List.of());
        List<LiberalRequiredCourseItemResponse> liberalRequired = liberalRequiredCourses.stream().map(
                status -> {
                    RequiredLiberalCategoryByCollege requiredLiberal = requiredLiberalCategoryByCollegeRepository.findById(status.getRequiredCourseId()).orElseThrow(() -> new RequiredLiberalCategoryNotFoundException(status.getRequiredCourseId()));

                    return new LiberalRequiredCourseItemResponse(requiredLiberal.getLiberalCategory().getName(), status.isFulfilled(), requiredLiberal.getRequiredCredit());
                }
        ).toList();


        return new UserRequiredCourseStatusResponse(majorRequired, liberalRequired);


    }
    @Transactional(readOnly = true)
    public List<UserGraduationRequirementStatusResponse> fetchUserGraduationRequirementStatus(Long userId){
        List<UserRequirementStatus> requirementStatuses = userRequirementStatusRepository.findAllByUserId(userId);
        if(requirementStatuses.isEmpty()) {
            throw new UserGraduationStatusNotFoundException(userId);
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

        UserMajor primaryMajor = userMajorRepository.findByUserIdAndMajorType(userId, MajorType.PRIMARY);
        Optional<UserMajor> secondMajor = userMajorRepository.findByUserIdAndMajorTypeIn(
                userId, List.of(MajorType.DOUBLE, MajorType.MINOR)
        );
        String secondMajorName = secondMajor
                .map(userMajor -> userMajor.getMajor().getName())
                .orElse(null);

        return new UserSummaryResponse(user.getUsername(), user.getStudentId(),profile.getJPA(),
                primaryMajor.getMajor().getName(),
                secondMajorName);
    }

    @Transactional
    public void updateUserMajor(Long userId, List<UserMajorUpdateRequest> requests){
        requests.forEach(
                request -> {
                    UserMajor userMajor = userMajorRepository.findByUserIdAndMajorType(userId, request.majorType());
                    Major major = majorRepository.findById(request.majorId()).orElseThrow(() -> new MajorNotFoundException(request.majorId()));
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
    public void updateCompletedCourses(List<CompletedCourseUpdateRequest> requests){
        requests.forEach(request -> {
            UserCompletedCourse userCompletedCourse = userCompletedCourseRepository.findById(request.userCompletedCourseId()).orElseThrow(() -> new UserCompletedCourseNotFoundException(request.userCompletedCourseId()));
            if (request.grade() != null) {
                userCompletedCourse.updateGrade(request.grade());
            }

            if (request.retake() != null) {
                userCompletedCourse.updateRetake(request.retake());
            }
        });

    }





}
