package com.taskforce.superinvention.app.domain.club.user

import com.querydsl.jpa.impl.JPAQueryFactory
import com.taskforce.superinvention.app.domain.club.ClubUser
import com.taskforce.superinvention.app.domain.club.QClubUser.*
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
class ClubUserRepositorySupport(
        var queryFactory: JPAQueryFactory
) : QuerydslRepositorySupport(ClubUser::class.java) {

    fun findByClubSeq(clubSeq: Long): List<ClubUser> =
            queryFactory.selectFrom(clubUser)
                    .where(clubUser.club.seq.eq(clubSeq)).fetch()
}