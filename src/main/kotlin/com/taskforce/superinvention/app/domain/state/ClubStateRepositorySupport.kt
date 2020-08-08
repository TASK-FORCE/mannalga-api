package com.taskforce.superinvention.app.domain.state

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
class ClubStateRepositorySupport : QuerydslRepositorySupport(ClubState::class.java){
}