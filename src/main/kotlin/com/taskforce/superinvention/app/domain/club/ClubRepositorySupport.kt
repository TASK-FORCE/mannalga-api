package com.taskforce.superinvention.app.domain.club

import com.querydsl.jpa.JPQLQuery
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
class ClubRepositorySupport : QuerydslRepositorySupport(Club::class.java) {
    fun findBySeq(seq: Long): Club {
        return from(QClub.club)
                .where(QClub.club.seq.eq(seq))
                .fetchOne()
    }

    fun findByKeyword(keyword: String): List<Club>? {
        return from(QClub.club)
                .where(QClub.club.name.contains(keyword))
                .fetch()
    }
}