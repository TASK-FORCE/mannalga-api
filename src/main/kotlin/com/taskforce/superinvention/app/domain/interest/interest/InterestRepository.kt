package com.taskforce.superinvention.app.domain.interest.interest

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface InterestRepository : JpaRepository<Interest, Long>,
                               InterestRepositoryCustom {
}

interface InterestRepositoryCustom {
}