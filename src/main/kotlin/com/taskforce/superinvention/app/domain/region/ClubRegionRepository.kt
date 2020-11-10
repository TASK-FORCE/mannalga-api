package com.taskforce.superinvention.app.domain.region

import com.taskforce.superinvention.app.domain.club.Club
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ClubRegionRepository : JpaRepository<ClubRegion, Long> {
    fun findByClub(club: Club): List<ClubRegion>
    fun findByClubSeq(clubSeq: Long): List<ClubRegion>?
}