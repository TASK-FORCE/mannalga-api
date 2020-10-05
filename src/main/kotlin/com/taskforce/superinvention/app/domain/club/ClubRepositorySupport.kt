package com.taskforce.superinvention.app.domain.club

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import com.taskforce.superinvention.app.domain.club.QClub.club
import com.taskforce.superinvention.app.domain.club.user.QClubUser.clubUser
import com.taskforce.superinvention.app.domain.interest.QClubInterest.clubInterest
import com.taskforce.superinvention.app.domain.region.QClubRegion.clubRegion
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.dto.club.ClubSearchOptions
import com.taskforce.superinvention.app.web.dto.interest.InterestRequestDto
import com.taskforce.superinvention.app.web.dto.region.RegionRequestDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository
import org.springframework.util.ObjectUtils

@Repository
class ClubRepositorySupport(val queryFactory:JPAQueryFactory) : QuerydslRepositorySupport(Club::class.java) {
    fun findByKeyword(keyword: String): List<Club>? {
        return from(club)
                .where(club.name.like(keyword))
                .fetch()
    }

    fun search(clubSearchOptions: ClubSearchOptions, pageable:Pageable): Page<Club> {
        val fetchResult = from(club)
                .leftJoin(club.clubInterests, clubInterest)
                .leftJoin(club.clubRegions, clubRegion)
                .where(
//                    eqInterests(clubSearchOptions.interestList)
//                            , eqStates(clubSearchOptions.stateList)
                )
                .groupBy(club)
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

    private fun eqRegions(regionList:List<RegionRequestDto>): BooleanExpression? {
        if (ObjectUtils.isEmpty(regionList)) return null
        return clubInterest.interest.seq.`in`(regionList.map { e -> e.seq })
    }

    fun getUserCount(clubSeq: Long): Long {
        return queryFactory.select(clubUser.user.count())
                .from(club)
                .leftJoin(club.clubUser, clubUser)
                .where(club.seq.eq(clubSeq))
                .fetchCount()
    }

    fun findByUser(userInfo: User, pageable: Pageable): Page<Club> {
        val fetchResults = queryFactory.select(club)
                .from(clubUser)
                .leftJoin(clubUser.club, club)
                .where(clubUser.user.seq.eq(userInfo.seq))
                .offset(pageable.offset)
                .limit(pageable.pageSize.toLong())
                .fetchResults()

        return PageImpl(fetchResults.results, pageable, fetchResults.total)
    }
}