package com.hyewon.wiseowl_backend.domain.user.repository;

import com.hyewon.wiseowl_backend.domain.course.entity.QCollege;
import com.hyewon.wiseowl_backend.domain.course.entity.QMajor;
import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;
import com.hyewon.wiseowl_backend.domain.user.dto.UserMajorDetail;
import com.hyewon.wiseowl_backend.domain.user.entity.QUser;
import com.hyewon.wiseowl_backend.domain.user.entity.QUserMajor;
import com.hyewon.wiseowl_backend.domain.user.entity.UserMajor;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class UserMajorQueryRepositoryImpl implements UserMajorQueryRepository {
    private final JPAQueryFactory query;
    private final QUserMajor userMajor = QUserMajor.userMajor;
    private final QUser user = QUser.user;
    private final QMajor major = QMajor.major;
    private final QCollege college = QCollege.college;

    @Override
    public Optional<UserMajorDetail> findUserMajorWithCollege(Long userId, MajorType majorType) {
        return Optional.ofNullable(
                query.select(Projections.constructor(UserMajorDetail.class,
                        college.id, college.name, major.id, major.name))
                .from(userMajor)
                .join(userMajor.user, user)
                .join(userMajor.major, major)
                .join(major.college, college)
                .where(
                        user.id.eq(userId),
                        userMajor.majorType.eq(majorType)
                ).fetchOne());
    }

    @Override
    public List<UserMajor> findAllByUserIdWithMajorAndCollege(Long userId) {
        return query
                .selectFrom(userMajor)
                .join(userMajor.major, major).fetchJoin()
                .join(major.college, college).fetchJoin()
                .where(userMajor.user.id.eq(userId))
                .fetch();
    }
}
