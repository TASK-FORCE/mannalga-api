package com.taskforce.superinvention.app.domain.region

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RegionRepository : JpaRepository<Region, Long> {
    fun findByLevel(level: Long): List<Region>
}