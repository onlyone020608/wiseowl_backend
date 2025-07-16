package com.hyewon.wiseowl_backend.domain.notice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String url;

    private LocalDate postedAt;

    @Enumerated(EnumType.STRING)
    private NoticeType noticeType;

    private Long sourceId;

    private String content;




}
