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
                                               @RequestBody @Valid ProfileUpdateRequest request){
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
    ){
        List<GraduationRequirementGroupByMajorResponse> graduationRequirementsForUser = userService.getGraduationRequirementsForUser(principal.getId());
        return ResponseEntity.ok(graduationRequirementsForUser);
    }

    @PostMapping("/me/graduation-requirements")
    public ResponseEntity<Void> updateRequirements(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Valid UserRequirementFulfillmentRequest request
            ) {
        userService.updateUserRequirementStatus(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("me/graduation-info")
    public ResponseEntity<MainPageGraduationStatusResponse> getMainGraduationInfo(
            @AuthenticationPrincipal UserPrincipal principal
    ){
        MainPageGraduationStatusResponse userGraduationOverview = userService.fetchUserGraduationOverview(principal.getId());
        return ResponseEntity.ok(userGraduationOverview);

    }

    @GetMapping("/users/me/required-courses")
    public ResponseEntity<UserRequiredCourseStatusResponse> getMyRequiredCourses(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam MajorType majorType
    ) {
        return ResponseEntity.ok(userService.fetchUserRequiredCourseStatus(principal.getId(), majorType));
    }

    @GetMapping("users/me/graduation-requirements")
    public ResponseEntity<List<UserGraduationRequirementStatusResponse>> getGraduationRequirementStatuses(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<UserGraduationRequirementStatusResponse> response =
                userService.fetchUserGraduationRequirementStatus(principal.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/summary")
    public UserSummaryResponse getSummary(@AuthenticationPrincipal UserPrincipal principal) {
        return userService.fetchUserSummary(principal.getId());
    }

    @PatchMapping("/api/users/me/majors")
    public ResponseEntity<Void> updateUserMajor(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Valid List<UserMajorUpdateRequest> requests
    ) {
        userService.updateUserMajor(principal.getId(), requests);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/api/users/me/majors/type")
    public ResponseEntity<Void> updateUserMajorTypes(
            @RequestBody @Valid List<UserMajorTypeUpdateRequest> requests
    ) {
        userService.updateUserMajorTypes(requests);
        return ResponseEntity.ok().build();
    }

}
