package com.hyewon.wiseowl_backend.domain.notice.repository;

import com.hyewon.wiseowl_backend.domain.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long>, NoticeQueryRepository {
}
