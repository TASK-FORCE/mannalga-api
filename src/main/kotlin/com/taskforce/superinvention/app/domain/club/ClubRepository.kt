package com.taskforce.superinvention.app.domain.club

import com.querydsl.core.QueryResults
import com.querydsl.core.Tuple
import com.querydsl.core.annotations.QueryProjection
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import com.taskforce.superinvention.app.domain.club.user.QClubUser
import com.taskforce.superinvention.app.domain.interest.QClubInterest
import com.taskforce.superinvention.app.domain.region.QClubRegion
import com.taskforce.superinvention.app.domain.role.QClubUserRole
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.dto.club.ClubSearchOptions
import com.taskforce.superinvention.app.web.dto.interest.InterestRequestDto
import com.taskforce.superinvention.app.web.dto.region.RegionRequestDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository
import org.springframework.util.ObjectUtils

interface ClubRepository : JpaRepository<Club, Long>, ClubRepositoryCustom {
    fun findBySeq(seq: Long): Club
}

interface ClubRepositoryCustom {
    fun search(clubSearchOptions: ClubSearchOptions, pageable: Pageable): Page<Club>
    fun findUserClubList(userInfo: User, pageable: Pageable): QueryResults<Tuple>
}

@Repository
class ClubRepositoryImpl(val queryFactory: JPAQueryFactory): ClubRepositoryCustom,
        QuerydslRepositorySupport(Club::class.java) {

    override fun search(clubSearchOptions: ClubSearchOptions, pageable: Pageable): Page<Club> {

        // SELECT FROM
        val query = from(QClub.club)
                .leftJoin(QClub.club.clubInterests, QClubInterest.clubInterest)
                .leftJoin(QClub.club.clubRegions, QClubRegion.clubRegion)

        // WHERE
        query.where(eqRegions(clubSearchOptions.regionList))
        query.where(eqInterests(clubSearchOptions.interestList))

        // PAGING
        val fetchResult = query
                .groupBy(QClub.club)
                .orderBy()
                .offset(pageable.offset)
                .limit(pageable.pageSize.toLong())
                .fetchResults()

        return PageImpl(fetchResult.results, pageable, fetchResult.total)
    }

    override fun findUserClubList(userInfo: User, pageable: Pageable): QueryResults<Tuple> {
        val clubUser = QClubUser.clubUser
        val clubUserRole = QClubUserRole.clubUserRole

        val groupConcatRole      = Expressions.stringTemplate("group_concat({0}, ' ')", clubUserRole.role.name)
        val groupConcatRoleGroup = Expressions.stringTemplate("group_concat({0}, ' ')", clubUserRole.role.roleGroup.name)

        val query =
                from(clubUserRole)
                        .select(
                                clubUserRole.clubUser.seq,
                                clubUserRole.clubUser.user.seq,
                                clubUserRole.clubUser.club,
                                clubUserRole.clubUser.seq.count(),

                                QRoleDtoQueryProjection(
                                        groupConcatRole,
                                        groupConcatRoleGroup
                                )
                        )
                        .join(clubUserRole.clubUser, clubUser)
                        .groupBy(clubUserRole.clubUser.club.seq)
                        .where(clubUserRole.clubUser.user.seq.eq(userInfo.seq))
                        .offset(pageable.offset)
                        .limit(pageable.pageSize.toLong())
                        .fetchResults()

        return query
    }

    private fun eqInterests(interestList:List<InterestRequestDto>): BooleanExpression? {
        if (ObjectUtils.isEmpty(interestList)) return null
        return QClubInterest.clubInterest.interest.seq.`in`(interestList.map { e -> e.seq })
    }

    private fun eqRegions(regionList:List<RegionRequestDto>): BooleanExpression? {
        if (ObjectUtils.isEmpty(regionList)) return null
        return QClubRegion.clubRegion.region.seq.`in`(regionList.map { e -> e.seq })
    }

    fun getUserCount(clubSeq: Long): Long {
        return queryFactory.select(QClubUser.clubUser.user.count())
                .from(QClub.club)
                .leftJoin(QClub.club.clubUser, QClubUser.clubUser)
                .where(QClub.club.seq.eq(clubSeq))
                .fetchCount()
    }

    fun findByUser(userInfo: User, pageable: Pageable): Page<Club> {
        val fetchResults = queryFactory.select(QClub.club)
                .from(QClubUser.clubUser)
                .leftJoin(QClubUser.clubUser.club, QClub.club)
                .where(QClubUser.clubUser.user.seq.eq(userInfo.seq))
                .offset(pageable.offset)
                .limit(pageable.pageSize.toLong())
                .fetchResults()

        return PageImpl(fetchResults.results, pageable, fetchResults.total)
    }
}

data class RoleDtoQueryProjection @QueryProjection constructor(
        val roleName     : String,
        val roleGroupName: String
)