package com.taskforce.superinvention.app.domain.user.userRegion

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRegionRepository : JpaRepository<UserRegion, Long> {

    fun findByUserSeq(userSeq: Long): List<UserRegion>
}