package com.taskforce.superinvention.app.domain.club.user

import com.querydsl.core.group.GroupBy.*
import com.querydsl.core.types.Predicate
import com.querydsl.jpa.JPAExpressions.selectFrom
import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.club.QClub
import com.taskforce.superinvention.app.domain.role.QClubUserRole
import com.taskforce.superinvention.app.domain.role.QRole
import com.taskforce.superinvention.app.domain.role.QRoleGroup
import com.taskforce.superinvention.app.domain.user.QUser
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.dto.club.ClubUserStatusDto
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
interface ClubUserRepository : JpaRepository<ClubUser, Long>, ClubUserRepositoryCustom {
    fun findBySeq(seq: Long): ClubUser
    fun findByClub(club: Club): List<ClubUser>
    fun findByClubSeq(clubSeq: Long): List<ClubUser>
    fun findByClubAndUser(club: Club, user: User): ClubUser?
    fun findByClubSeqAndUser(clubSeq: Long, user: User): ClubUser?
    fun findByClubSeqAndUserSeq(clubSeq: Long, userSeq: Long): ClubUser?
}

interface ClubUserRepositoryCustom {
    fun findClubUserWithRole(clubSeq: Long, pUser: User): ClubUser?
    fun findClubUsersInClub(clubSeq: Long): List<ClubUser>
}

@Repository
class ClubUserRepositoryImpl: ClubUserRepositoryCustom,
    QuerydslRepositorySupport(ClubUser::class.java) {

    override fun findClubUserWithRole(clubSeq: Long, pUser: User): ClubUser? {
        val user = QUser.user
        val club = QClub.club
        val clubUser = QClubUser.clubUser
        val clubUserRole = QClubUserRole.clubUserRole

        val query = from(clubUser)
                .join(clubUser.club, club).fetchJoin()
                .join(clubUser.user, user).fetchJoin()
                .join(clubUser.clubUserRoles, clubUserRole).fetchJoin()
                .where(clubUser.club.seq.eq(clubSeq)
                    .and(clubUser.user.seq.eq(pUser.seq)))

        return query.fetchOne()
    }

    // 클럽원들의 유저 정보 조회
    override fun findClubUsersInClub(clubSeq: Long): List<ClubUser> {
        var clubUserRole = QClubUserRole.clubUserRole
        var clubUser = QClubUser.clubUser
        var user = QUser.user

        val query = from(clubUser)
            .join(clubUser.clubUserRoles, clubUserRole).fetchJoin()
            .join(clubUser.user, user).fetchJoin()
            .where(eqSeq(clubUser.club, clubSeq))

        return query.fetch()
    }

    private fun eqSeq(club: QClub, clubSeq: Long): Predicate {
        return club.seq.eq(clubSeq)
    }
}