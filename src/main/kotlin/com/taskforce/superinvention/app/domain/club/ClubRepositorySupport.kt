package com.taskforce.superinvention.app.domain.club

import com.querydsl.jpa.JPQLQuery
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository
import com.taskforce.superinvention.app.domain.club.QClub.club

@Repository
class ClubRepositorySupport : QuerydslRepositorySupport(Club::class.java) {
    fun findBySeq(seq: Long): Club {
        return from(club)
                .where(club.seq.eq(seq))
                .fetchOne()
    }

    fun findByKeyword(page: Long, keyword: String): List<Club>? {
        return from(club)
                .where(club.name.contains(keyword))
                .orderBy(club.createdAt.desc())
                .offset(page*10)
                .limit(10L)
                .fetch()
    }

}