package com.taskforce.superinvention.app.domain.club

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ClubUserRepository : JpaRepository<ClubUser, Long> {
    fun findByClub(club: Club): List<ClubUser>
}