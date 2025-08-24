package com.hyewon.wiseowl_backend.domain;

import com.hyewon.wiseowl_backend.domain.course.entity.Major;
import com.hyewon.wiseowl_backend.domain.course.repository.MajorRepository;
import com.hyewon.wiseowl_backend.domain.course.service.MajorQueryService;
import com.hyewon.wiseowl_backend.fixture.CourseFixture;
import com.hyewon.wiseowl_backend.global.exception.MajorNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class MajorQueryServiceTest {
    @InjectMocks MajorQueryService majorQueryService;
    @Mock MajorRepository majorRepository;

    private Major major;

    @BeforeEach
    void setUp() {
        major = CourseFixture.aMajor();
    }

    @Test
    @DisplayName("getMajorName - should return major name")
    void shouldReturnMajorName_whenIdExists() {
        // given
        Long majorId = 1L;
        given(majorRepository.findById(majorId)).willReturn(
                Optional.of(major));

        // when
        String majorName = majorQueryService.getMajorName(majorId);

        // then
        assertThat(majorName).isEqualTo("컴퓨터공학과");
    }

    @Test
    @DisplayName("getMajorName - should throw MajorNotFoundException when major does not exist")
    void shouldThrowException_whenMajorNotFoundInGetMajorName() {
        // given
        Long majorId = 999L;
        given(majorRepository.findById(majorId)).willReturn(
                Optional.empty());

        // when & then
        assertThrows(MajorNotFoundException.class,
                () ->  majorQueryService.getMajorName(majorId));
    }

    @Test
    @DisplayName("getMajor - should return major")
    void shouldReturnMajor_whenIdExists() {
        // given
        Long majorId = 1L;
        given(majorRepository.findById(majorId)).willReturn(
                Optional.of(major));

        // when
        Major responseMajor = majorQueryService.getMajor(majorId);

        // then
       assertThat(responseMajor).isEqualTo(major);
    }

    @Test
    @DisplayName("getMajor - should throw MajorNotFoundException when major does not exist")
    void shouldThrowException_whenMajorNotFoundInGetMajor() {
        // given
        Long majorId = 999L;
        given(majorRepository.findById(majorId)).willReturn(
                Optional.empty());

        // when & then
        assertThrows(MajorNotFoundException.class,
                () ->  majorQueryService.getMajor(majorId));
    }
}
