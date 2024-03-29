package com.taskforce.superinvention.app.domain.interest.interest

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import com.taskforce.superinvention.app.domain.interest.interest.QInterest.interest
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
class InterestRepositoryImpl(
        private val query: JPAQueryFactory
) : QuerydslRepositorySupport(Interest::class.java),
    InterestRepositoryCustom
{
    fun findBySeq(seq: Long): Interest {
        return from(interest)
                .where(interest.seq.eq(seq))
                .fetchOne()
    }
}