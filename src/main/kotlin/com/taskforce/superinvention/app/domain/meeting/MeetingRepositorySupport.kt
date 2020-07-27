package com.taskforce.superinvention.app.domain.meeting

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
class MeetingRepositorySupport : QuerydslRepositorySupport(Meeting::class.java){
}