package com.taskforce.superinvention.app.domain.role

import com.taskforce.superinvention.app.domain.club.user.ClubUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ClubUserRoleRepository : JpaRepository<ClubUserRole, Long> {
    fun findByClubUser(clubUser: ClubUser): Set<ClubUserRole>
}