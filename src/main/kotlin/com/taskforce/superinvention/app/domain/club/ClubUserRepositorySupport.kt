package com.taskforce.superinvention.app.domain.club

import com.querydsl.jpa.impl.JPAQueryFactory
import com.taskforce.superinvention.app.domain.club.QClubUser.*
import com.taskforce.superinvention.app.domain.user.User
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
class ClubUserRepositorySupport(
        var queryFactory: JPAQueryFactory
) : QuerydslRepositorySupport(ClubUser::class.java) {

    fun findBySeq(seq: Long): ClubUser {
        return from(QClubUser.clubUser)
                .where(QClubUser.clubUser.seq.eq(seq))
                .fetchOne()
    }

    fun findByClubSeq(clubSeq: Long): List<ClubUser> =
            queryFactory.selectFrom(clubUser).where(clubUser.club.seq.eq(clubSeq)).fetch()

    fun findByUserSeq(user: User): List<ClubUser> =
            queryFactory.selectFrom(clubUser).where(clubUser.user.seq.eq(user.seq)).fetch()

}