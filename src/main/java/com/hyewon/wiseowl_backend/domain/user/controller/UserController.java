package com.hyewon.wiseowl_backend.domain.user.controller;

import com.hyewon.wiseowl_backend.domain.auth.security.UserPrincipal;
import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;
import com.hyewon.wiseowl_backend.domain.user.dto.*;
import com.hyewon.wiseowl_backend.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/users")
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/me/profile")
    public ResponseEntity<Void> updateProfile( @AuthenticationPrincipal UserPrincipal principal,
                                               @RequestBody @Valid ProfileUpdateRequest request) {
       userService.updateUserProfile(principal.getId(), request);
        return ResponseEntity.ok().build();
   }

    @PostMapping("/me/completed-courses")
    public ResponseEntity<Void> insertCompletedCourses(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Valid CompletedCourseInsertRequest request
    ) {
        userService.insertCompletedCourses(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/me/graduation-requirements")
    public ResponseEntity<List<GraduationRequirementGroupByMajorResponse>> getGraduationRequirements(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<GraduationRequirementGroupByMajorResponse> graduationRequirementsForUser = userService.getGraduationRequirementsForUser(principal.getId());
        return ResponseEntity.ok(graduationRequirementsForUser);
    }

    @PutMapping("/me/graduation-requirements")
    public ResponseEntity<Void> updateRequirements(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Valid UserRequirementFulfillmentRequest request
            ) {
        userService.updateUserRequirementStatus(principal.getId(), request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/graduation-info")
    public ResponseEntity<MainPageGraduationStatusResponse> getMainGraduationInfo(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        MainPageGraduationStatusResponse userGraduationOverview = userService.getUserGraduationOverview(principal.getId());
        return ResponseEntity.ok(userGraduationOverview);
    }

    @GetMapping("/me/required-courses")
    public ResponseEntity<UserRequiredCourseStatusResponse> getMyRequiredCourses(@AuthenticationPrincipal UserPrincipal principal,
                                                                                 @RequestParam MajorType majorType) {
        return ResponseEntity.ok(userService.getUserRequiredCourseStatus(principal.getId(), majorType));
    }

    @GetMapping("/me/summary")
    public UserSummaryResponse getSummary(@AuthenticationPrincipal UserPrincipal principal) {
        return userService.fetchUserSummary(principal.getId());
    }

    @PatchMapping("/me/majors")
    public ResponseEntity<Void> updateUserMajor(@AuthenticationPrincipal UserPrincipal principal,
                                                @RequestBody @Valid List<UserMajorUpdateRequest> requests) {
        userService.updateUserMajor(principal.getId(), requests);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/me/majors/type")
    public ResponseEntity<Void> updateUserMajorTypes(@RequestBody @Valid List<UserMajorTypeUpdateRequest> requests) {
        userService.updateUserMajorTypes(requests);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/me/completed-courses")
    public ResponseEntity<Void> updateCompletedCourses(@AuthenticationPrincipal UserPrincipal principal,
                                                       @RequestBody @Valid List<CompletedCourseUpdateRequest> requests) {
        userService.updateCompletedCourses(principal.getId(), requests);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/me/subscriptions")
    public ResponseEntity<Void> subscribeOrganizations(@AuthenticationPrincipal UserPrincipal principal,
                                                       @RequestBody @Valid List<UserSubscriptionRequest> requests) {
        userService.registerUserSubscriptions(principal.getId(), requests);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/me/subscriptions")
    public ResponseEntity<Void> updateUserSubscriptions(@AuthenticationPrincipal UserPrincipal principal,
                                                        @RequestBody List<UserSubscriptionRequest> requests) {
        userService.replaceAllUserSubscriptions(principal.getId(), requests);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal UserPrincipal principal) {
        userService.deleteUser(principal.getId());
        return ResponseEntity.noContent().build();
    }
}
