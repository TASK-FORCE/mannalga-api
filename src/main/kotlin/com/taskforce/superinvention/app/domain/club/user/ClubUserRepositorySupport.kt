package com.taskforce.superinvention.app.domain.club.user

import com.querydsl.jpa.impl.JPAQueryFactory
import com.taskforce.superinvention.app.domain.club.user.QClubUser.clubUser
import com.taskforce.superinvention.app.domain.user.QUser.user
import com.taskforce.superinvention.app.domain.user.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
class ClubUserRepositorySupport(
        var queryFactory: JPAQueryFactory
) : QuerydslRepositorySupport(ClubUser::class.java) {

    fun findBySeq(seq: Long): ClubUser {
        return from(clubUser)
                .where(clubUser.seq.eq(seq))
                .fetchOne()
    }

    fun findByClubSeq(clubSeq: Long): List<ClubUser> =
            queryFactory.selectFrom(clubUser).where(clubUser.club.seq.eq(clubSeq)).fetch()

    fun findByUserSeq(userSeq: Long): List<ClubUser> =
            queryFactory.selectFrom(clubUser).where(clubUser.user.seq.eq(userSeq)).fetch()

    fun findByUser(userInfo: User, pageable: Pageable): Page<ClubUser> {
        val result = from(clubUser)
                .leftJoin(clubUser.user, user)
                .where(user.seq.eq(userInfo.seq))
                .offset(pageable.offset)
                .limit(pageable.pageSize.toLong())
                .fetchResults()
        return PageImpl(result.results, pageable, result.total)
    }
}