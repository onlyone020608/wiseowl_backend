package com.hyewon.wiseowl_backend.domain.user.controller;

import com.hyewon.wiseowl_backend.domain.auth.security.UserPrincipal;
import com.hyewon.wiseowl_backend.domain.user.dto.CompletedCourseUpdateRequest;
import com.hyewon.wiseowl_backend.domain.user.dto.GraduationRequirementGroupByMajorResponse;
import com.hyewon.wiseowl_backend.domain.user.dto.ProfileUpdateRequest;
import com.hyewon.wiseowl_backend.domain.user.dto.UserRequirementFulfillmentRequest;
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
            @RequestBody @Valid  CompletedCourseUpdateRequest request
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
}
