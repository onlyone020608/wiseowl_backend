package com.hyewon.wiseowl_backend.domain.user.controller;

import com.hyewon.wiseowl_backend.domain.auth.security.UserPrincipal;
import com.hyewon.wiseowl_backend.domain.user.dto.CompletedCourseUpdateRequest;
import com.hyewon.wiseowl_backend.domain.user.dto.ProfileUpdateRequest;
import com.hyewon.wiseowl_backend.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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
}
