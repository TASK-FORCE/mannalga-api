package com.taskforce.superinvention.app.domain.club.user

import com.querydsl.core.types.Predicate
import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.club.QClub
import com.taskforce.superinvention.app.domain.role.QClubUserRole
import com.taskforce.superinvention.app.domain.role.QRole
import com.taskforce.superinvention.app.domain.role.Role
import com.taskforce.superinvention.app.domain.user.QUser
import com.taskforce.superinvention.app.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
interface ClubUserRepository : JpaRepository<ClubUser, Long>, ClubUserRepositoryCustom {
    fun findBySeq(seq: Long): ClubUser
    fun findByClubAndUser(club: Club, user: User): ClubUser?
    fun findByClubSeqAndUser(clubSeq: Long, user: User): ClubUser?
    fun findByClubSeqAndUserSeq(clubSeq: Long, userSeq: Long): ClubUser?
}

interface ClubUserRepositoryCustom {
    fun findClubUserWithRole(clubSeq: Long, pUser: User): ClubUser?
    fun findClubUsersInClub(clubSeq: Long): List<ClubUser>
    fun findByClub(club: Club): List<ClubUser>
    fun findByClubSeq(clubSeq: Long): List<ClubUser>
    fun findManagersByClubSeq(clubSeq: Long): MutableList<ClubUser>
    fun findMasterByClubSeq(clubSeq: Long): ClubUser
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
        val role = QRole.role

        val query = from(clubUser)
            .join(clubUser.clubUserRoles, clubUserRole).fetchJoin()
            .join(clubUser.user, user).fetchJoin()
            .leftJoin(clubUser.clubUserRoles, clubUserRole).fetchJoin()
            .leftJoin(clubUserRole.role, role).fetchJoin()
            .where(eqSeq(clubUser.club, clubSeq), role.name.`in`(Role.RoleName.CLUB_MEMBER, Role.RoleName.MANAGER, Role.RoleName.MASTER))
            .orderBy(clubUser.clubUserRoles.any().role.level.desc(), clubUser.user.userName.asc())

        return query.fetch()
    }

    private fun eqSeq(club: QClub, clubSeq: Long): Predicate {
        return club.seq.eq(clubSeq)
    }

    override fun findManagersByClubSeq(clubSeq: Long): MutableList<ClubUser> {
        return from(QClubUser.clubUser)
                .leftJoin(QClubUser.clubUser.clubUserRoles, QClubUserRole.clubUserRole).fetchJoin()
                .leftJoin(QClubUserRole.clubUserRole.role, QRole.role).fetchJoin()
                .join(QClubUser.clubUser.club, QClub.club)
                .join(QClubUser.clubUser.user, QUser.user)
                .where(
                        QClub.club.seq.eq(clubSeq),
                        QRole.role.name.`in`(Role.RoleName.MANAGER, Role.RoleName.MASTER)
                ).fetch()?: mutableListOf()
    }

    override fun findByClub(targetClub: Club): List<ClubUser> {
        val club = QClub.club
        val clubUser = QClubUser.clubUser
        val user = QUser.user
        val clubUserRole = QClubUserRole.clubUserRole
        val role = QRole.role

        return from(clubUser)
            .join(clubUser.club, club)
            .join(clubUser.user, user)
            .leftJoin(clubUser.clubUserRoles, clubUserRole).fetchJoin()
            .leftJoin(clubUserRole.role, role).fetchJoin()
            .where(club.eq(targetClub), role.name.`in`(Role.RoleName.CLUB_MEMBER, Role.RoleName.MANAGER, Role.RoleName.MASTER))
            .fetch()

    }

    override fun findByClubSeq(clubSeq: Long): List<ClubUser> {
        return findByClub(from(QClub.club).where(QClub.club.seq.eq(clubSeq)).fetchOne())
    }

    override fun findMasterByClubSeq(clubSeq: Long): ClubUser {
        return from(QClubUser.clubUser)
            .leftJoin(QClubUser.clubUser.clubUserRoles, QClubUserRole.clubUserRole).fetchJoin()
            .leftJoin(QClubUserRole.clubUserRole.role, QRole.role).fetchJoin()
            .join(QClubUser.clubUser.club, QClub.club)
            .join(QClubUser.clubUser.user, QUser.user)
            .where(
                QClub.club.seq.eq(clubSeq),
                QRole.role.name.`in`(Role.RoleName.MASTER)
            ).fetchOne()
    }
}