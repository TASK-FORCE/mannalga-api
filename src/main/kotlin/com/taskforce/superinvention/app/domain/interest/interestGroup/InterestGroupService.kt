package com.taskforce.superinvention.app.domain.interest.interestGroup

import org.springframework.stereotype.Service

@Service
class InterestGroupService(
        private val interestGroupRepo: InterestGroupRepository
) {

    fun getInterestList(): List<InterestGroupDto> {
        val result = interestGroupRepo.findAllInterestGroupList()
        return result
    }
}