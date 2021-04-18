package com.taskforce.superinvention.app.domain.user.userRole

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRoleRepository : JpaRepository<UserRole, Long> {
    fun findByUserSeqAndRoleName(userSeq: Long, roleName: String): UserRole?
}