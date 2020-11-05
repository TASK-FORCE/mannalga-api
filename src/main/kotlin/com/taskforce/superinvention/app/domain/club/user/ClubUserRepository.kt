package com.taskforce.superinvention.app.domain.club.user

import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ClubUserRepository : JpaRepository<ClubUser, Long> {
    fun findBySeq(seq: Long): ClubUser
    fun findByClub(club: Club): List<ClubUser>
    fun findByClubSeq(clubSeq: Long): List<ClubUser>
    fun findByClubAndUser(club: Club, user: User): ClubUser
    fun findByClubSeqAndUser(clubSeq: Long, user: User): ClubUser?
    fun findByClubSeqAndUserSeq(clubSeq: Long, userSeq: Long): ClubUser?
}