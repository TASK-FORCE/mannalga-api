package com.taskforce.superinvention.app.domain.interest

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
class UserInterestRepositorySupport : QuerydslRepositorySupport(UserInterest::class.java) {
    fun findBySeq(seq: Long): UserInterest {
        return from(QUserInterest.userInterest)
                .where(QUserInterest.userInterest.seq.eq(seq))
                .fetchOne()
    }
}