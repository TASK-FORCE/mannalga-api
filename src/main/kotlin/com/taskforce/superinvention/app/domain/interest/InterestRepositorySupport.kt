package com.taskforce.superinvention.app.domain.interest

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
class InterestRepositorySupport : QuerydslRepositorySupport(Interest::class.java){
    fun findBySeq(seq: Long): Interest {
        return from(QInterest.interest)
                .where(QInterest.interest.seq.eq(seq))
                .fetchOne()
    }
}