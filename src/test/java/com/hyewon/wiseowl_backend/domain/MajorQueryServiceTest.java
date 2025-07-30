package com.hyewon.wiseowl_backend.domain;

import com.hyewon.wiseowl_backend.domain.course.entity.Major;
import com.hyewon.wiseowl_backend.domain.course.repository.MajorRepository;
import com.hyewon.wiseowl_backend.domain.course.service.MajorQueryService;
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

    @InjectMocks
    MajorQueryService majorQueryService;

    @Mock
    MajorRepository majorRepository;

    private Major major;
    @BeforeEach
    void setUp() {
        major = Major.builder()
                .id(1L)
                .name("컴퓨터공학과")
                .build();

    }


    @Test
    @DisplayName("getMajorName - should return major name")
    void getMajorName_shouldSucceed() {
        // given
        Long majorId = 1L;
        given(majorRepository.findById(majorId)).willReturn(
                Optional.of(major));

        // when
        String majorName = majorQueryService.getMajorName(majorId);

        // then
        assertThat(majorName).isEqualTo(major.getName());
    }

    @Test
    @DisplayName("getMajorName - should throw MajorNotFoundException when major does not exist")
    void getMajorName_shouldThrowException_whenMajorNotFound() {
        // given
        Long majorId = 999L;
        given(majorRepository.findById(majorId)).willReturn(
                Optional.empty());

        // when & then
        assertThrows(MajorNotFoundException.class,
                () ->  majorQueryService.getMajorName(majorId));
    }
}
