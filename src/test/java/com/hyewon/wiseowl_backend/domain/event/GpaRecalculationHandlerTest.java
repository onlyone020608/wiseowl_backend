package com.hyewon.wiseowl_backend.domain.event;

import com.hyewon.wiseowl_backend.domain.user.dto.CreditAndGradeDto;
import com.hyewon.wiseowl_backend.domain.user.entity.Grade;
import com.hyewon.wiseowl_backend.domain.user.entity.Profile;
import com.hyewon.wiseowl_backend.domain.user.entity.User;
import com.hyewon.wiseowl_backend.domain.user.entity.UserCompletedCourse;
import com.hyewon.wiseowl_backend.domain.user.event.CompletedCoursesRegisteredEvent;
import com.hyewon.wiseowl_backend.domain.user.event.GpaRecalculationHandler;
import com.hyewon.wiseowl_backend.domain.user.repository.ProfileRepository;
import com.hyewon.wiseowl_backend.domain.user.repository.UserCompletedCourseRepository;
import com.hyewon.wiseowl_backend.global.exception.ProfileNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class GpaRecalculationHandlerTest {
    @InjectMocks
    private GpaRecalculationHandler gpaRecalculationHandler;

    @Mock
    private UserCompletedCourseRepository userCompletedCourseRepository;
    @Mock
    private ProfileRepository profileRepository;

    private User user;
    private Profile profile;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .build();
        profile = Profile.builder()
                .user(user)
                .GPA(2.9)
                .build();


    }

    @Test
    @DisplayName("handle() - should recalculate GPA and update profile when event is received")
    void handle_shouldUpdateGpa_whenEventReceived() {
        // given
        Long userId = 1L;
        CreditAndGradeDto cag1 = new CreditAndGradeDto(3, Grade.A);
        CreditAndGradeDto cag2 = new CreditAndGradeDto(3, Grade.B);
        CreditAndGradeDto cag3 = new CreditAndGradeDto(3, Grade.C);


        given(userCompletedCourseRepository.findCourseCreditsAndGradesByUserId(userId)).willReturn(
                List.of(cag1, cag2, cag3)
        );
        given(profileRepository.findByUserId(userId)).willReturn(Optional.of(profile));
        List<UserCompletedCourse> dummyCourses = List.of();

        // when
        gpaRecalculationHandler.handle(new CompletedCoursesRegisteredEvent(userId, dummyCourses));
        // then
        assertThat(profile.getGPA()).isEqualTo(3.0);



    }

    @Test
    @DisplayName("handle() - should throw exception when profile not found")
    void handle_shouldThrow_whenProfileNotFound() {
        // given
        Long userId = 1L;
        CreditAndGradeDto cag1 = new CreditAndGradeDto(3, Grade.A);
        CreditAndGradeDto cag2 = new CreditAndGradeDto(3, Grade.B);
        CreditAndGradeDto cag3 = new CreditAndGradeDto(3, Grade.C);


        given(userCompletedCourseRepository.findCourseCreditsAndGradesByUserId(userId)).willReturn(
                List.of(cag1, cag2, cag3)
        );
        given(profileRepository.findByUserId(userId)).willReturn(Optional.empty());
        List<UserCompletedCourse> dummyCourses = List.of();

        // when & then
        assertThrows(ProfileNotFoundException.class, () -> gpaRecalculationHandler.handle(new CompletedCoursesRegisteredEvent(userId, dummyCourses)) );



    }
}
