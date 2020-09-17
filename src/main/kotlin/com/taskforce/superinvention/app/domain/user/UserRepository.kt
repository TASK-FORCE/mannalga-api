package com.taskforce.superinvention.app.domain.user

import com.taskforce.superinvention.app.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByUserId(userId: String): User?
}