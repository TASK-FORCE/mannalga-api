package com.taskforce.superinvention.app.domain.meeting

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MeetingApplicationRepository : JpaRepository<MeetingApplication, Long> {
}