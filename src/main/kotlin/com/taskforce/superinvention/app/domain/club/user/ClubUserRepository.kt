package com.taskforce.superinvention.app.domain.club.user

import com.querydsl.core.annotations.QueryProjection
import com.querydsl.core.types.dsl.Expressions
import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.role.*
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

@Repository
interface ClubUserRepository : JpaRepository<ClubUser, Long>, ClubUserRepositoryCustom {
    fun findBySeq(seq: Long): ClubUser
    fun findByClub(club: Club): List<ClubUser>
    fun findByClubSeq(clubSeq: Long): List<ClubUser>
    fun findByClubAndUser(club: Club, user: User): ClubUser
    fun findByClubSeqAndUser(clubSeq: Long, user: User): ClubUser
    fun findByClubSeqAndUserSeq(clubSeq: Long, userSeq: Long): ClubUser?
    fun countByClubSeq(club: Long): Long
}

interface ClubUserRepositoryCustom {
    fun findByUserWithPaging(userInfo: User, pageable: Pageable): Page<ClubUserDto>
}

@Repository
class ClubUserRepositoryImpl: ClubUserRepositoryCustom,
        QuerydslRepositorySupport(ClubUser::class.java) {

    override fun findByUserWithPaging(userInfo: User, pageable: Pageable): Page<ClubUserDto> {
        val clubUser = QClubUser.clubUser
        val clubUserRole = QClubUserRole.clubUserRole

        val groupConcatRole      = Expressions.stringTemplate("group_concat({0}, ' ')", clubUserRole.role.name)
        val groupConcatRoleGroup = Expressions.stringTemplate("group_concat({0}, ' ')", clubUserRole.role.roleGroup.name)

        val sql =
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

        val result = sql.results.map { tuple ->
            ClubUserDto(
                seq     = tuple.get(0, Long::class.java)!!,
                userSeq = tuple.get(1, Long::class.java)!!,
                club    = ClubDto(
                        tuple.get(2, Club::class.java)!!,
                        tuple.get(3, Long::class.java)!!
                ),
                roles = toRoleSet(tuple.get(4, RoleDtoQueryProjection::class.java))
            )
        }

        return PageImpl(result, pageable, sql.total)
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