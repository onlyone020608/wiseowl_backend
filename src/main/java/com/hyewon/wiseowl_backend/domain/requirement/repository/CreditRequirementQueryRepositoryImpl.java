package com.hyewon.wiseowl_backend.domain.requirement.repository;

import com.hyewon.wiseowl_backend.domain.course.entity.CourseType;
import com.hyewon.wiseowl_backend.domain.course.entity.Major;
import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;
import com.hyewon.wiseowl_backend.domain.requirement.entity.QCreditRequirement;
import com.hyewon.wiseowl_backend.domain.requirement.entity.Track;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreditRequirementQueryRepositoryImpl implements CreditRequirementQueryRepository {
    private final JPAQueryFactory query;
    private final QCreditRequirement cr = QCreditRequirement.creditRequirement;

    @Override
    public int sumRequiredCredits(Major major, MajorType majorType, Track track, Integer entranceYear) {
        Integer credit = query.select(cr.requiredCredits)
                .where(
                        cr.major.eq(major),
                        cr.majorType.eq(majorType),
                        cr.track.eq(track),
                        cr.appliesFromYear.coalesce(0).loe(entranceYear),
                        cr.appliesToYear.coalesce(Integer.MAX_VALUE).goe(entranceYear)
                ).fetchOne();
        return credit == null ? 0 : credit;
    }
}
