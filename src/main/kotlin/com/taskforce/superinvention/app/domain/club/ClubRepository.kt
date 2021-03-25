package com.taskforce.superinvention.app.domain.club

import com.querydsl.core.Tuple
import com.querydsl.core.annotations.QueryProjection
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.taskforce.superinvention.app.domain.club.QClub.*
import com.taskforce.superinvention.app.domain.club.user.QClubUser
import com.taskforce.superinvention.app.domain.club.user.QClubUser.clubUser
import com.taskforce.superinvention.app.domain.interest.QClubInterest.*
import com.taskforce.superinvention.app.domain.interest.interest.QInterest
import com.taskforce.superinvention.app.domain.interest.interest.QInterest.interest
import com.taskforce.superinvention.app.domain.interest.interestGroup.QInterestGroup
import com.taskforce.superinvention.app.domain.interest.interestGroup.QInterestGroup.interestGroup
import com.taskforce.superinvention.app.domain.region.QClubRegion.*
import com.taskforce.superinvention.app.domain.region.QRegion
import com.taskforce.superinvention.app.domain.region.QRegion.region
import com.taskforce.superinvention.app.domain.role.QClubUserRole
import com.taskforce.superinvention.app.domain.role.QRoleGroup
import com.taskforce.superinvention.app.domain.role.Role
import com.taskforce.superinvention.app.domain.user.QUser.user
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.dto.club.ClubDto
import com.taskforce.superinvention.app.web.dto.club.ClubUserDto
import com.taskforce.superinvention.app.web.dto.role.RoleDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

interface ClubRepository : JpaRepository<Club, Long>, ClubRepositoryCustom {
    fun findBySeq(seq: Long): Club
}

interface ClubRepositoryCustom {
    fun search(text: String?, regionSeqList: List<Long>, interestSeq: Long?, interestGroupSeq: Long?, pageable: Pageable): Page<Club>
    fun findUserClubList(userInfo: User, pageable: Pageable): Page<ClubUserDto>
    fun findClubInfo(clubSeq: Long): Tuple?
}

@Repository
class ClubRepositoryImpl: ClubRepositoryCustom, QuerydslRepositorySupport(Club::class.java) {

    override fun search(text: String?, regionSeqList: List<Long>, interestSeq: Long?, interestGroupSeq: Long?, pageable: Pageable): Page<Club> {

        val query = from(club)
                .join(club.clubUser, clubUser).fetchJoin()
                .join(clubUser.user, user).fetchJoin()
                .join(club.clubInterests, clubInterest).fetchJoin()
                .join(clubInterest.interest, interest).fetchJoin()
                .join(interest.interestGroup, interestGroup).fetchJoin()
                .join(club.clubRegions, clubRegion).fetchJoin()
                .join(clubRegion.region, region).fetchJoin()
                .where(
                    inIfNotEmpty(clubRegion.region, regionSeqList),
                    eqIfExist(clubInterest.interest, interestSeq),
                    eqIfExist(clubInterest.interest.interestGroup, interestGroupSeq),
                    searchIfExist(club, text)
                )

        // PAGING
        val fetchResult = query
                .groupBy(club)
                .orderBy(club.createdAt.desc())
                .offset(pageable.offset)
                .limit(pageable.pageSize.toLong())
                .fetchResults()

        return PageImpl(fetchResult.results, pageable, fetchResult.total)
    }

    private fun searchIfExist(club: QClub, searchText: String?): BooleanExpression? {

        if(searchText.isNullOrBlank()) {
            return null
        }

        return club.name.contains(searchText)
            .or(club.description.contains(searchText))
    }

    private fun eqIfExist(interest: QInterest, interestSeq: Long?): BooleanExpression? {

        if(interestSeq == null) {
            return null
        }

        return interest.seq.eq(interestSeq)
    }

    private fun eqIfExist(interestGroup: QInterestGroup, interestSeq: Long?): BooleanExpression? {
        if(interestSeq == null) {
            return null
        }

        return interestGroup.seq.eq(interestSeq)
    }

    private fun inIfNotEmpty(region: QRegion, regionSeqList: List<Long>): BooleanExpression? {

        if(regionSeqList.isEmpty()) {
            return null
        }

        return region.seq.`in`(regionSeqList)
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
        val roleGroup = QRoleGroup.roleGroup;

        val groupConcatRole      = Expressions.stringTemplate("group_concat({0})", clubUserRole.role.name)
        val groupConcatRoleGroup = Expressions.stringTemplate("group_concat({0})", clubUserRole.role.roleGroup.name)

        val query =
                from(clubUserRole)
                        .select(
                                clubUserRole.clubUser.seq,
                                clubUserRole.clubUser.user.seq,
                                clubUserRole.clubUser.club,

                                QRoleDtoQueryProjection(
                                        groupConcatRole,
                                        groupConcatRoleGroup
                                )
                        )
                        .join(clubUserRole.clubUser, clubUser)
                        .join(clubUserRole.role.roleGroup, roleGroup)
                        .groupBy(clubUserRole.clubUser.club.seq)
                        .where(clubUserRole.clubUser.user.seq.eq(userInfo.seq),
                            clubUserRole.role.name.notIn(Role.RoleName.NONE, Role.RoleName.MEMBER))
        if (pageable.isPaged) {
            query.offset(pageable.offset)
                .limit(pageable.pageSize.toLong())
        }

        val results = query.fetchResults()

        val result: List<ClubUserDto> = results.results.map { tuple ->
            ClubUserDto(
                    seq = tuple.get(0, Long::class.java)!!,
                    userSeq = tuple.get(1, Long::class.java)!!,
                    club = ClubDto(
                            tuple.get(2, Club::class.java)!!
                    ),
                    roles = toRoleSet(tuple.get(3, RoleDtoQueryProjection::class.java))
            )
        }

        return PageImpl(result, pageable, results.total)
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
