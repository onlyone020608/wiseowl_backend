package com.hyewon.wiseowl_backend.domain.user.repository;

import com.hyewon.wiseowl_backend.domain.course.entity.CourseType;
import com.hyewon.wiseowl_backend.domain.course.entity.QCourse;
import com.hyewon.wiseowl_backend.domain.course.entity.QLiberalCategory;
import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;
import com.hyewon.wiseowl_backend.domain.requirement.entity.QRequiredLiberalCategoryByCollege;
import com.hyewon.wiseowl_backend.domain.requirement.entity.QRequiredMajorCourse;
import com.hyewon.wiseowl_backend.domain.user.dto.LiberalRequiredCourseItemResponse;
import com.hyewon.wiseowl_backend.domain.user.dto.MajorRequiredCourseItemResponse;
import com.hyewon.wiseowl_backend.domain.user.entity.QUser;
import com.hyewon.wiseowl_backend.domain.user.entity.QUserRequiredCourseStatus;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class UserRequiredCourseStatusQueryRepositoryImpl implements UserRequiredCourseStatusQueryRepository {
    private final JPAQueryFactory query;
    private final QUserRequiredCourseStatus urs = QUserRequiredCourseStatus.userRequiredCourseStatus;
    private final QRequiredMajorCourse requiredMajorCourse = QRequiredMajorCourse.requiredMajorCourse;
    private final QRequiredLiberalCategoryByCollege requiredLiberal = QRequiredLiberalCategoryByCollege.requiredLiberalCategoryByCollege;
    private final QLiberalCategory liberalCategory = QLiberalCategory.liberalCategory;
    private final QCourse c = QCourse.course;
    private final QUser user = QUser.user;

    @Override
    public List<MajorRequiredCourseItemResponse> findMajorItems(Long userId, MajorType majorType) {
        return query.select(Projections.constructor(MajorRequiredCourseItemResponse.class,
                        c.courseCodePrefix, c.name, urs.fulfilled))
                .from(urs)
                .join(urs.user, user)
                .join(requiredMajorCourse).on(requiredMajorCourse.id.eq(urs.requiredCourseId))
                .join(requiredMajorCourse.course, c)
                .where(
                        user.id.eq(userId),
                        urs.courseType.eq(CourseType.MAJOR),
                        requiredMajorCourse.majorType.eq(majorType)
                ).fetch();
    }

    @Override
    public List<LiberalRequiredCourseItemResponse> findLiberalItems(Long userId) {
        return query.select(Projections.constructor(LiberalRequiredCourseItemResponse.class,
                liberalCategory.name, urs.fulfilled, requiredLiberal.requiredCredit
                ))
                .from(urs)
                .join(urs.user, user)
                .join(requiredLiberal).on(requiredLiberal.id.eq(urs.requiredCourseId))
                .join(requiredLiberal.liberalCategory, liberalCategory)
                .where(
                        user.id.eq(userId),
                        urs.courseType.eq(CourseType.GENERAL)
                ).fetch();
    }
}
