package com.taskforce.superinvention.app.domain.board

import com.taskforce.superinvention.app.domain.club.Club
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ClubRepository : JpaRepository<Club, Long> {

}