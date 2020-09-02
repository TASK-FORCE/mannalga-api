package com.taskforce.superinvention.app.domain.club

import com.taskforce.superinvention.app.domain.interest.UserInterest
import com.taskforce.superinvention.app.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository

interface ClubRepository : JpaRepository<Club, Long> {
    fun findBySeq(seq: Long): Club?
}