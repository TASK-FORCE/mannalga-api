package com.taskforce.superinvention.app.domain.interest.interestGroup

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface InterestGroupRepository : JpaRepository<InterestGroup, Long>,
                                    InterestGroupRepositoryCustom

interface InterestGroupRepositoryCustom {
      fun findAllInterestGroupList(): List<InterestGroupDto>
}