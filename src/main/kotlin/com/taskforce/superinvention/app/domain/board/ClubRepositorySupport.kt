package com.taskforce.superinvention.app.domain.board

import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.club.QClub
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
class ClubRepositorySupport : QuerydslRepositorySupport(Club::class.java) {

    fun findBySeq(seq: Long): Club {
        return from(QClub.club)
                .where(QClub.club.seq.eq(seq))
                .fetchOne()
    }

}