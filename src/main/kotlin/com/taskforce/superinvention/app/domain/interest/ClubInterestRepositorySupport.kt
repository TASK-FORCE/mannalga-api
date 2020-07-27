package com.taskforce.superinvention.app.domain.interest

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
class ClubInterestRepositorySupport : QuerydslRepositorySupport(ClubInterest::class.java){
    fun findBySeq(seq: Long): ClubInterest {
        return from(QClubInterest.clubInterest)
                .where(QClubInterest.clubInterest.seq.eq(seq))
                .fetchOne()
    }
}