package com.hyewon.wiseowl_backend.domain.notice.controller;

import com.hyewon.wiseowl_backend.domain.auth.security.UserPrincipal;
import com.hyewon.wiseowl_backend.domain.notice.dto.NoticeResponse;
import com.hyewon.wiseowl_backend.domain.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/notices")
@RestController
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;

    @GetMapping("/subscribed")
    public ResponseEntity<List<NoticeResponse>> getSubscribedNotices(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<NoticeResponse> notices = noticeService.getUserSubscribedNotices(principal.getId());
        return ResponseEntity.ok(notices);
    }
}
