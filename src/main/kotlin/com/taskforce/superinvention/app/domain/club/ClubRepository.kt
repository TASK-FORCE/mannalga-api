package com.taskforce.superinvention.app.domain.club

import org.springframework.data.jpa.repository.JpaRepository

interface ClubRepository : JpaRepository<Club, Long> {
}