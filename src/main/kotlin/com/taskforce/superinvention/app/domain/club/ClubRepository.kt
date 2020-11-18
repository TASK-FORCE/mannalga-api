package com.taskforce.superinvention.app.domain.club

import com.querydsl.core.QueryResults
import com.querydsl.core.Tuple
import com.querydsl.core.annotations.QueryProjection
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.JPQLQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import com.taskforce.superinvention.app.domain.club.QClub.*
import com.taskforce.superinvention.app.domain.club.user.QClubUser
import com.taskforce.superinvention.app.domain.interest.QClubInterest
import com.taskforce.superinvention.app.domain.interest.QClubInterest.*
import com.taskforce.superinvention.app.domain.interest.interest.QInterest
import com.taskforce.superinvention.app.domain.interest.interest.QInterest.*
import com.taskforce.superinvention.app.domain.interest.interestGroup.QInterestGroup
import com.taskforce.superinvention.app.domain.interest.interestGroup.QInterestGroup.*
import com.taskforce.superinvention.app.domain.region.QClubRegion
import com.taskforce.superinvention.app.domain.region.QClubRegion.*
import com.taskforce.superinvention.app.domain.region.QRegion
import com.taskforce.superinvention.app.domain.role.QClubUserRole
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.dto.club.ClubDto
import com.taskforce.superinvention.app.web.dto.club.ClubUserDto
import com.taskforce.superinvention.app.web.dto.interest.InterestRequestDto
import com.taskforce.superinvention.app.web.dto.region.RegionRequestDto
import com.taskforce.superinvention.app.web.dto.role.RoleDto
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
    fun search(regionSeqList: List<Long?>, interestSeq: Long?, interestGroupSeq: Long?, pageable: Pageable): Page<Club>
    fun findUserClubList(userInfo: User, pageable: Pageable): Page<ClubUserDto>
    fun findClubInfo(clubSeq: Long): Tuple?
}

@Repository
class ClubRepositoryImpl(val queryFactory: JPAQueryFactory): ClubRepositoryCustom,
        QuerydslRepositorySupport(Club::class.java) {

    override fun search(regionSeqList: List<Long?>, interestSeq: Long?, interestGroupSeq: Long?, pageable: Pageable): Page<Club> {

        // SELECT FROM
        val query = from(club)
                .leftJoin(club.clubInterests, clubInterest)
                .leftJoin(club.clubRegions, clubRegion)

        // WHERE
        if (regionSeqList.isNotEmpty()) query.where(clubRegion.region.seq.`in`(regionSeqList))
        if (interestSeq != null) query.where(clubInterest.interest.seq.eq(interestSeq))
        if (interestGroupSeq != null)
            query.where(clubInterest.interest.seq.`in`(
                JPAExpressions.select(interest.seq)
                        .from(interestGroup)
                        .leftJoin(interestGroup.interestList, interest)
                        .where(interestGroup.seq.eq(interestGroupSeq))
                )
            )

        // PAGING
        val fetchResult = query
                .groupBy(club)
                .orderBy()
                .offset(pageable.offset)
                .limit(pageable.pageSize.toLong())
                .fetchResults()

        return PageImpl(fetchResult.results, pageable, fetchResult.total)
    }

    override fun findClubInfo(clubSeq: Long): Tuple? {
        val club = club
        val clubUser = QClubUser.clubUser

        val query =
                from(clubUser)
                .select(
                        clubUser.club,
                        clubUser.seq.count()
                )
                .join(clubUser.club, club)
                .groupBy(clubUser.club.seq)
                .where(clubUser.club.seq.eq(clubSeq))
                .fetchFirst()

        return query
    }

    override fun findUserClubList(userInfo: User, pageable: Pageable): Page<ClubUserDto> {
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

        val result = query.results.map { tuple ->
            ClubUserDto(
                    seq = tuple.get(0, Long::class.java)!!,
                    userSeq = tuple.get(1, Long::class.java)!!,
                    club = ClubDto(
                            tuple.get(2, Club::class.java)!!,
                            tuple.get(3, Long::class.java)!!
                    ),
                    roles = toRoleSet(tuple.get(4, RoleDtoQueryProjection::class.java))
            )
        }

        return PageImpl(result, pageable, query.total)

    }

    private fun toRoleSet(concatedRole: RoleDtoQueryProjection?): Set<RoleDto> {
        if(concatedRole == null) return setOf()

        val roleNames= concatedRole.roleName.split(",")
        val roleGroupNames =  concatedRole.roleGroupName.split(",")

        val roleSet = mutableSetOf<RoleDto>()
        for(x in roleNames.indices) {
            roleSet.add(RoleDto("ROLE_${roleNames[x]}", roleGroupNames[x]))
        }
        return roleSet
    }
}

data class RoleDtoQueryProjection @QueryProjection constructor(
        val roleName     : String,
        val roleGroupName: String
)