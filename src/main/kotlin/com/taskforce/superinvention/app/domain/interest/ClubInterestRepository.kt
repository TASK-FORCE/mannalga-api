package com.taskforce.superinvention.app.domain.interest

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ClubInterestRepository : JpaRepository<ClubInterest, Long> {
}