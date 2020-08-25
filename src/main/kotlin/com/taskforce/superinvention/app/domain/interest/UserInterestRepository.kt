package com.taskforce.superinvention.app.domain.interest

import com.taskforce.superinvention.app.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserInterestRepository : JpaRepository<UserInterest, Long> {
    fun findByUser(user: User): List<UserInterest>
}