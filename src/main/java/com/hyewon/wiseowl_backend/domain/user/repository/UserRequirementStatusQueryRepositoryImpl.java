package com.hyewon.wiseowl_backend.domain.user.repository;

import com.hyewon.wiseowl_backend.domain.course.entity.Major;
import com.hyewon.wiseowl_backend.domain.course.entity.QMajor;
import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;
import com.hyewon.wiseowl_backend.domain.requirement.entity.QMajorRequirement;
import com.hyewon.wiseowl_backend.domain.user.entity.QUser;
import com.hyewon.wiseowl_backend.domain.user.entity.QUserRequirementStatus;
import com.hyewon.wiseowl_backend.domain.user.entity.UserRequirementStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class UserRequirementStatusQueryRepositoryImpl implements UserRequirementStatusQueryRepository {
    private final JPAQueryFactory query;

    @Override
    public List<UserRequirementStatus> findByUserAndMajor(Long userId, Major major, MajorType majorType) {
        QUserRequirementStatus urs = QUserRequirementStatus.userRequirementStatus;
        QMajorRequirement mr =  QMajorRequirement.majorRequirement;
        QMajor m = QMajor.major;
        QUser user = QUser.user;
        return query.select(urs)
                .from(urs)
                .join(urs.user, user)
                .join(urs.majorRequirement, mr).fetchJoin()
                .join(mr.major, m).fetchJoin()
                .where(
                        user.id.eq(userId),
                        m.eq(major),
                        mr.majorType.eq(majorType)
                )
                .fetch();
    }
}
