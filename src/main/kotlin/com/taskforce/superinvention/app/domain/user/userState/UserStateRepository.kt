package com.taskforce.superinvention.app.domain.user.userState

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserStateRepository : JpaRepository<UserState, Long> {

    fun findByUserSeq(userSeq: Long): List<UserState>
}