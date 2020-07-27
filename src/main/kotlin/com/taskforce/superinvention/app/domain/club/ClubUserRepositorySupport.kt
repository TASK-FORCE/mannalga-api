package com.taskforce.superinvention.app.domain.club

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
class ClubUserRepositorySupport : QuerydslRepositorySupport(ClubUser::class.java) {
    fun findBySeq(seq: Long): ClubUser {
        return from(QClubUser.clubUser)
                .where(QClubUser.clubUser.seq.eq(seq))
                .fetchOne()
    }
}