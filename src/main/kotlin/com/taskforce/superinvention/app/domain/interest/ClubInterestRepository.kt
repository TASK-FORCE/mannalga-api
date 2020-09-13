package com.taskforce.superinvention.app.domain.interest

import com.taskforce.superinvention.app.domain.club.Club
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ClubInterestRepository : JpaRepository<ClubInterest, Long> {
    fun findByClub(club: Club): List<ClubInterest>
}