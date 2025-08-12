package com.hyewon.wiseowl_backend.domain.requirement.repository;

import com.hyewon.wiseowl_backend.domain.course.entity.QMajor;
import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorRequirement;
import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;
import com.hyewon.wiseowl_backend.domain.requirement.entity.QMajorRequirement;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class MajorRequirementQueryRepositoryImpl implements MajorRequirementQueryRepository {
    private final JPAQueryFactory query;

    @Override
    public List<MajorRequirement> findApplicable(Long majorId, MajorType majorType, Integer entranceYear) {
        QMajorRequirement mr = QMajorRequirement.majorRequirement;
        QMajor m = QMajor.major;

        return query.select(mr)
                .from(mr)
                .join(mr.major, m)
                .where(
                        m.id.eq(majorId),
                        mr.majorType.eq(majorType),
                        mr.appliesFromYear.coalesce(0).loe(entranceYear),
                        mr.appliesToYear.coalesce(Integer.MAX_VALUE).goe(entranceYear)

                )
                .fetch();
    }
}
