package com.taskforce.superinvention.app.domain.interest.interestGroup

import com.taskforce.superinvention.config.DataJpaRepoTest
import org.springframework.beans.factory.annotation.Autowired

class InterestGroupRepoTestData : DataJpaRepoTest() {

    @Autowired
    lateinit var interestGroupRepo: InterestGroupRepository
}