package com.taskforce.superinvention.app.domain.club

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import com.taskforce.superinvention.app.domain.club.QClub.club
import com.taskforce.superinvention.app.domain.club.QClubUser.clubUser
import com.taskforce.superinvention.app.domain.interest.QClubInterest.clubInterest
import com.taskforce.superinvention.app.web.dto.club.ClubSearchOptions
import com.taskforce.superinvention.app.web.dto.interest.InterestRequestDto
import com.taskforce.superinvention.app.web.dto.state.StateRequestDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository
import org.springframework.util.ObjectUtils

@Repository
class ClubRepositorySupport(val queryFactory:JPAQueryFactory) : QuerydslRepositorySupport(Club::class.java) {
    fun findBySeq(seq: Long): Club {
        return from(club)
                .where(club.seq.eq(seq))
                .fetchOne()
    }

    fun findByKeyword(keyword: String): List<Club>? {
        return from(club)
                .where(club.name.like(keyword))
                .fetch()
    }

    fun search(clubSearchOptions: ClubSearchOptions, pageable:Pageable): Page<Club> {
        val fetchResult = queryFactory
                .selectFrom(club)
                .leftJoin(clubInterest.club, club)
                .where(
                    eqInterests(clubSearchOptions.interestList)
                    , eqStates(clubSearchOptions.stateList)
                )
                .orderBy()
                .offset(pageable.offset)
                .limit(pageable.pageSize.toLong())
                .fetchResults()
        return PageImpl(fetchResult.results, pageable, fetchResult.total)
    }

    private fun eqInterests(interestList:List<InterestRequestDto>): BooleanExpression? {
        if (ObjectUtils.isEmpty(interestList)) return null
        return clubInterest.interest.seq.`in`(interestList.map { e -> e.seq })
    }

    private fun eqStates(stateList:List<StateRequestDto>): BooleanExpression? {
        if (ObjectUtils.isEmpty(stateList)) return null
        return clubInterest.interest.seq.`in`(stateList.map { e -> e.seq })
    }

    fun getUserCount(clubSeq: Long): Long {
        return queryFactory.select(clubUser.user.count())
                .from(club)
                .leftJoin(clubUser.club, club)
                .where(club.seq.eq(clubSeq))
                .fetchCount()
    }
}