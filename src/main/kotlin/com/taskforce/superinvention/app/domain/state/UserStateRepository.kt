package com.taskforce.superinvention.app.domain.state

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserStateRepository : JpaRepository<UserState, Long>{
}